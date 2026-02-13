package org.spring.steganography.Model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class StegoImg {

    @Id
    private String id;
    private String userId;
    private String encodedImgUrl;
    private LocalDateTime createdAt;
    private long imgSize;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEncodedImgUrl() {
        return encodedImgUrl;
    }

    public void setEncodedImgUrl(String encodedImgUrl) {
        this.encodedImgUrl = encodedImgUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getImgSize() {
        return imgSize;
    }

    public void setImgSize(long imgSize) {
        this.imgSize = imgSize;
    }
}
