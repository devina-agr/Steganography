package org.spring.steganography.Util;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityUtils {
    public static String hashToken(String token){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] encodedHash=digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        }
        catch(Exception e){
            throw new IllegalStateException("Error hashing token", e);
        }
    }
}
