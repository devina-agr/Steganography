package org.spring.steganography.DTO.UserDTO;

import lombok.Builder;
import org.spring.steganography.Model.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public class UserResponse {

    private String id;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
