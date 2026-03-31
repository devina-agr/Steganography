package org.spring.steganography.Service;

import org.spring.steganography.Exception.InvalidInviteException;
import org.spring.steganography.Model.AdminInvite;
import org.spring.steganography.Repository.AdminInviteRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminInviteService {


    private final AdminInviteRepo adminInviteRepo;
    private final EmailService emailService;

    public AdminInviteService(AdminInviteRepo adminInviteRepo, EmailService emailService) {
        this.adminInviteRepo = adminInviteRepo;
        this.emailService = emailService;
    }

    public void sendInvite(String username, String email) {
        String token= UUID.randomUUID().toString();
        AdminInvite adminInvite=new AdminInvite();
        adminInvite.setEmail(email);
        adminInvite.setInvitedBy(username);
        adminInvite.setInviteToken(token);
        adminInvite.setUsed(false);
        adminInvite.setExpiresAt(LocalDateTime.now().plusHours(5));
        adminInvite.setSentAt(LocalDateTime.now());
        adminInviteRepo.save(adminInvite);
        emailService.sendEmail(
                email,
                "Admin Invite",
                """
                        You are invited as ADMIN.
                        Click the link below to register:
                         http://localhost:5173/admin-register?token=%s
                         This link will expire in 5 hours.
                     """
                        .formatted(token)

        );
    }

    public AdminInvite validateInvite(String token){
        AdminInvite invite=adminInviteRepo.findByInviteToken(token).orElseThrow(()->new RuntimeException("Invalid invite"));
        if(invite.isUsed()){
            throw new InvalidInviteException("Invite already used!");
        }
        if(invite.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidInviteException("Invite expired!");
        }
        return invite;
    }


    public void markUsed(AdminInvite invite){
        invite.setUsed(true);
        adminInviteRepo.save(invite);
    }

}
