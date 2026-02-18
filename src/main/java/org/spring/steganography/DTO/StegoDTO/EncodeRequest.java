package org.spring.steganography.DTO.StegoDTO;

import jakarta.validation.constraints.NotBlank;

public class EncodeRequest {

    @NotBlank
    private String secretText;

    public @NotBlank String getSecretText() {
        return secretText;
    }

    public void setSecretText(@NotBlank String secretText) {
        this.secretText = secretText;
    }
}
