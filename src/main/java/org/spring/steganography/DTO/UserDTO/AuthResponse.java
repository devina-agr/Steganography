package org.spring.steganography.DTO.UserDTO;

import org.spring.steganography.Model.Role;

import java.util.List;
import java.util.Set;

public class AuthResponse {

    private String token;
    private String email;
    private List<String> role;

    public AuthResponse(String token, String email, List<String> role) {
        this.token=token;
        this.email=email;
        this.role=role;
    }

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

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }
}
