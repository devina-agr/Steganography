package org.spring.steganography.DTO.StegoDTO;

import lombok.Builder;

@Builder
public class StegoResponse {

    private String id;
    private String encodedImageUrl;
    private String secretKey;

    public StegoResponse(String id, String encodedImageUrl, String secretKey) {
        this.id = id;
        this.encodedImageUrl = encodedImageUrl;
        this.secretKey = secretKey;
    }

    public String getId() {
        return id;
    }

    public String getEncodedImageUrl() {
        return encodedImageUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
