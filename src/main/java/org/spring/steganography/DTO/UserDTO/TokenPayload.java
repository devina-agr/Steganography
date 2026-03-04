package org.spring.steganography.DTO.UserDTO;

public record TokenPayload (
    String email,
    int tokenVersion
){}
