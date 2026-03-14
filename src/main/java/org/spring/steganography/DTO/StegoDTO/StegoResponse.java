package org.spring.steganography.DTO.StegoDTO;

import lombok.Builder;

@Builder
public class StegoResponse {

    private String recordId;
    private String encodedImageUrl;
    private String secretKey;

    public StegoResponse(String recordId, String encodedImageUrl, String secretKey) {
        this.recordId = recordId;
        this.encodedImageUrl = encodedImageUrl;
        this.secretKey = secretKey;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getEncodedImageUrl() {
        return encodedImageUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
