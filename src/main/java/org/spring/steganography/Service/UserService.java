package org.spring.steganography.Service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.spring.steganography.Exception.InvalidInviteException;
import org.spring.steganography.Exception.TokenExpiredException;
import org.spring.steganography.Exception.UnAuthorizedActionException;
import org.spring.steganography.Exception.UserNotFoundException;
import org.spring.steganography.Model.TokenType;
import org.spring.steganography.Model.User;
import org.spring.steganography.Model.VerificationToken;
import org.spring.steganography.Repository.UserRepo;
import org.spring.steganography.Repository.VerificationTokenRepository;
import org.spring.steganography.Util.SecurityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AdminService adminService;

    public UserService(BCryptPasswordEncoder passwordEncoder, UserRepo userRepo, EmailService emailService, VerificationTokenRepository verificationTokenRepository, AdminService adminService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.adminService = adminService;
    }

    public User register(@NotBlank @Email String email, @NotBlank String password) {
        if(userRepo.findByEmail(email).isPresent()){
            throw new UnAuthorizedActionException("User already exists. Please login or register with a different email.");
        }
        User user=User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .tokenVersion(0)
                .build();
        return userRepo.save(user);
    }

    public User getByEmail(@NotBlank @Email String email) {
        return userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public void changePassword(String id, String oldPassword, String newPassword) {
        User user=adminService.getById(id);
        if(!passwordEncoder.matches(oldPassword,user.getPassword())){
             throw new UnAuthorizedActionException("Old password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion()+1);
        userRepo.save(user);
        emailService.sendEmail(
                user.getEmail(),
                "Password Changed",
                """
                        Your password has been changed successfully.
                        If this wasn't you, please reset your password immediately.
                      """
        );
    }

    public void requestEmailChange(String userId, String newEmail, String password) {
        User user=adminService.getById(userId);
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new UnAuthorizedActionException("Invalid password!");
        }
        if(userRepo.findByEmail(newEmail).isPresent()){
            throw new UnAuthorizedActionException("Email already exists");
        }
        String rawToken=UUID.randomUUID().toString();
        String token= SecurityUtils.hashToken(rawToken);
        VerificationToken verificationToken=VerificationToken.builder()
                .userId(user.getId())
                .token(token)
                .type(TokenType.EMAIL_CHANGE)
                .newValue(newEmail)
                .expiry(LocalDateTime.now().plusHours(5))
                .build();
        verificationTokenRepository.save(verificationToken);

        emailService.sendEmail(
                newEmail,
                "Confirm Email Change",
                """
                        Click the link below to confirm your email change:
                         http://localhost:8080/api/users/email/confirm?token=%s
                        This link will expire in 5 hours.
                      """
                        .formatted(rawToken)
        );
    }

    public void confirmEmailChange(String token) {
        String hashToken=SecurityUtils.hashToken(token);
        VerificationToken verificationToken=verificationTokenRepository.findByToken(hashToken).orElseThrow(()->new InvalidInviteException("Invalid token"));
        if(verificationToken.getType()!=TokenType.EMAIL_CHANGE){
            throw new UnAuthorizedActionException("Invalid token type");
        }
        if(verificationToken.getExpiry().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token expired!");
        }
        User user=adminService.getById(verificationToken.getUserId());
        user.setEmail(verificationToken.getNewValue());
        user.setTokenVersion(user.getTokenVersion()+1);
        userRepo.save(user);
        verificationTokenRepository.delete(verificationToken);

        emailService.sendEmail(
                user.getEmail(),
                "Email Changed Successfully",
                """
                        Your email address has been changed successfully.
                        If this wasn't you, please contact support immediately.
                      """
        );
    }

    public void forgotPassword(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            String rawToken= UUID.randomUUID().toString();
            String token=SecurityUtils.hashToken(rawToken);
            VerificationToken verificationToken=VerificationToken.builder()
                    .userId(user.getId())
                    .token(token)
                    .type(TokenType.PASSWORD_RESET)
                    .expiry(LocalDateTime.now().plusHours(5))
                    .build();
            verificationTokenRepository.save(verificationToken);
            emailService.sendEmail(
                    user.getEmail(),
                    "Reset Your Password",
                    """
                            Click the link below to reset password:
                             http://localhost:8080/api/users/reset-password?token=%s
                            This link will expire in 5 hours.
                         """
                            .formatted(rawToken)
            );
        });
    }

    public void resetPassword(String token, String newPassword) {
        String hashToken=SecurityUtils.hashToken(token);
        VerificationToken verificationToken=verificationTokenRepository.findByToken(hashToken).orElseThrow(()->new InvalidInviteException("Invalid token"));
        if(verificationToken.getType()!=TokenType.PASSWORD_RESET){
            throw new UnAuthorizedActionException("Invalid token type");
        }
        if(verificationToken.getExpiry().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token expired.");
        }
        User user=adminService.getById(verificationToken.getUserId());
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion()+1);
        userRepo.save(user);
        verificationTokenRepository.delete(verificationToken);

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Successfully",
                """
                        Your password has been reset successfully.
                        If you did not perform this action,
                        please contact support immediately.
                      """
        );
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredToken(){
        verificationTokenRepository.deleteByExpiryBefore(LocalDateTime.now());
    }

}
