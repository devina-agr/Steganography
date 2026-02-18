package org.spring.steganography.DTO.UserDTO;

import java.time.LocalDateTime;

public class AdminInviteResponse {

    private String invitedToEmail;
    private String invitedByEmail;
    private String inviteToken;
    private LocalDateTime expiresAt;
    private boolean used;

    public String getInvitedToEmail() {
        return invitedToEmail;
    }

    public void setInvitedToEmail(String invitedToEmail) {
        this.invitedToEmail = invitedToEmail;
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
