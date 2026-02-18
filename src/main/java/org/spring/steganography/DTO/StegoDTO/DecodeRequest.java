package org.spring.steganography.DTO.StegoDTO;

import jakarta.validation.constraints.NotBlank;

public class DecodeRequest {

    @NotBlank
    private String secretKey;

    public @NotBlank String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(@NotBlank String secretKey) {
        this.secretKey = secretKey;
    }
}
