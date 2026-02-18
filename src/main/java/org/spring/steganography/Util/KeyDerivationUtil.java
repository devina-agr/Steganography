package org.spring.steganography.Util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyDerivationUtil {

    private static final int ITERATIONS=65536;
    private static final int KEY_LENGTH=256;

    public static byte[] generateSalt(){
        byte[] salt=new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static byte[] deriveKey(char[] password, byte[] salt){
        try{
            PBEKeySpec spec=new PBEKeySpec(password,salt,ITERATIONS,KEY_LENGTH);
            SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        }
        catch(Exception e){
            throw new RuntimeException("Key derivation failed ",e);
        }
    }

    public static String hashKey(byte[] key){
        try{
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            byte[] hash=messageDigest.digest(key);
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (Exception e){
            throw new RuntimeException("key hashing failed ",e);
        }
    }


}
