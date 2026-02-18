package org.spring.steganography.DTO.UserDTO;

import org.spring.steganography.Model.Role;

import java.util.Set;

public class AuthResponse {

    private String token;
    private String email;
    private Set<Role> role;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRole() {
        return role;
    }

    public void setRole(Set<Role> role) {
        this.role = role;
    }
}
