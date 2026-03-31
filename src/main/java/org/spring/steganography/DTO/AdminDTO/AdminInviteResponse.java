package org.spring.steganography.DTO.AdminDTO;

import java.time.LocalDateTime;

public class AdminInviteResponse {

    private String email;
    private String invitedByEmail;
    private String inviteToken;
    private LocalDateTime expiresAt;
    private boolean used;

    public AdminInviteResponse(String email, String invitedBy, String inviteToken, LocalDateTime expiresAt, boolean used) {
        this.invitedByEmail=invitedBy;
        this.email=email;
        this.inviteToken=inviteToken;
        this.expiresAt=expiresAt;
        this.used=used;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvitedByEmail() {
        return invitedByEmail;
    }

    public void setInvitedByEmail(String invitedByEmail) {
        this.invitedByEmail = invitedByEmail;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
