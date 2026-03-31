package org.spring.steganography.DTO.AdminDTO;

import jakarta.validation.constraints.NotBlank;

public class AdminRegisterRequest {

    @NotBlank
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
