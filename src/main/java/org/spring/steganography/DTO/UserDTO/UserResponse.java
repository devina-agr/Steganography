package org.spring.steganography.DTO.UserDTO;

import lombok.Builder;
import org.spring.steganography.Model.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public class UserResponse {

    private String id;
    private String email;
    private Set<String> role;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
