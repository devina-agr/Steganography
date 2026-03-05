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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    public UserService(BCryptPasswordEncoder passwordEncoder, UserRepo userRepo, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public User register(@NotBlank @Email String email, @NotBlank String password) {
        if(userRepo.findByEmail(email).isEmpty()){
            throw new RuntimeException("User already exists. Please login or register with a different email.");
        }

    }

    public User getByEmail(@NotBlank @Email String email) {
        return userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public User getById(String id) {
        return userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void deleteUser(String id) {
        User user=userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not found!"));
        userRepo.delete(user);
    }

    public void changePassword(String id, String oldPassword, String newPassword) {
        User user=getById(id);
        if(!passwordEncoder.matches(user.getPassword(), oldPassword)){
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
    }

    public void confirmEmailChange(String token) {
    }

    public void forgotPassword(String email) {
        User user =userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found"));
        String token= UUID.randomUUID().toString();
        VerificationToken verificationToken=VerificationToken.builder()
                .userId(user.getId())
                .token(token)
                .type(TokenType.PASSWORD_RESET)
                .expiry(LocalDateTime.now().plusHours(5))
                .build();
        verificationTokenRepository.save(verificationToken);
    }

    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken=verificationTokenRepository.findByToken(token).orElseThrow(()->new InvalidInviteException("Invalid token"));
        if(verificationToken.getType()!=TokenType.PASSWORD_RESET){
            throw new UnAuthorizedActionException("Invalid token type");
        }
        if(verificationToken.getExpiry().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token expired.");
        }
        User user=getById(verificationToken.getUserId());
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
}
