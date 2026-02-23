package org.spring.steganography.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JWTServices {

    private final String secret_key;
    private final long expiration_value;

    public JWTServices(@Value("jwt.secret") String secret_key,@Value("jwt.expiration") long expiration_value) {
        this.secret_key = secret_key;
        this.expiration_value = expiration_value;
    }

    private Key getSigningKey(){
        return
    }

}
