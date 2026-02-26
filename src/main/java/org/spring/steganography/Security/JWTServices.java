package org.spring.steganography.Security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JWTServices {

    private final String secret_key;
    private final long expiration_value;

    public JWTServices(@Value("jwt.secret") String secret_key,@Value("jwt.expiration") long expiration_value) {
        this.secret_key = secret_key;
        this.expiration_value = expiration_value;
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secret_key.getBytes());
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token,String email){
        String extractedEmail=extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration=Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date());
    }
}
