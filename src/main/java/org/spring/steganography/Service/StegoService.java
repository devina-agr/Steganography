package org.spring.steganography.Service;

import jakarta.validation.constraints.NotBlank;
import org.spring.steganography.DTO.StegoDTO.DecodeRequest;
import org.spring.steganography.DTO.StegoDTO.StegoResponse;
import org.spring.steganography.Exception.UnAuthorizedActionException;
import org.spring.steganography.Exception.UserNotFoundException;
import org.spring.steganography.Model.StegoRecords;
import org.spring.steganography.Repository.StegoRecordsRepo;
import org.spring.steganography.Util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

import static java.util.Objects.hash;


@Service
public class StegoService {
    private final StegoRecordsRepo stegoRecordsRepo;

    public StegoService(StegoRecordsRepo stegoRecordsRepo) {
        this.stegoRecordsRepo = stegoRecordsRepo;
    }

    public StegoResponse encodeMessage(String userId, MultipartFile image, @NotBlank String secretText) {
        validateImage(image);
        String secretKey=generateSecretKey();
        String encryptedMessage=encrypt(secretText,secretKey);
        byte[] encodedImage=hideMessage(image,encryptedMessage);
        String fileUrl=saveImage(encodedImage);
        StegoRecords record=StegoRecords.builder()
                .userId(userId)
                .imgName(image.getOriginalFilename())
                .encodedImgUrl(fileUrl)
                .createdAt(LocalDateTime.now())
                .imgSize(image.getSize())
                .secretKeyHash(SecurityUtils.hashToken(secretKey))
                .build();
        stegoRecordsRepo.save(record);
        return new StegoResponse(record.getId(),fileUrl,secretKey);
    }

    private String saveImage(byte[] encodedImage) {
    }

    private byte[] hideMessage(MultipartFile image, String encryptedMessage) {

    }

    public String decodeMessage(String userId, MultipartFile image, DecodeRequest request) {
        validateImage(image);
        String hiddenMessage=extractMessage(image);
        return decrypt(hiddenMessage,request.getSecretKey());
    }

    private String extractMessage(MultipartFile image) {
    }

    public Page<StegoResponse> getUserRecords(String userId, int page, int size) {
        return stegoRecordsRepo.findByUserId(userId, PageRequest.of(page,size))
                               .map(record->new StegoResponse(record.getRecordId(),record.getEncodedImageUrl(),null));
    }

    public byte [] downloadImage(String userId, String recordId) {
        StegoRecords records=stegoRecordsRepo.findById(recordId).orElseThrow(()->new UserNotFoundException("Records not found!"));

        if(!records.getUserId().equals(userId)){
            throw new UnAuthorizedActionException("Access denied!");
        }
        return loadImage(records.getEncodedImgUrl());
    }

    private byte[] loadImage(String encodedImgUrl) {
    }

    public void deleteRecord(String userId, String recordId) {
        StegoRecords records=stegoRecordsRepo.findById(recordId).orElseThrow(()->new UserNotFoundException("Record not found!"));
        if(!records.getUserId().equals(userId)){
            throw new UnAuthorizedActionException("Access denied!");
        }
        stegoRecordsRepo.delete(records);
    }

    private void validateImage(MultipartFile image){
        if(image.isEmpty()){
            throw new IllegalArgumentException("Image cannot be empty.");
        }
        if(!image.getContentType().startsWith("image")){
            throw new IllegalArgumentException("Invalid file type");
        }
    }

    private String generateSecretKey(){
        byte[] key=new byte[16];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private String encrypt(String message, String key){
        try{
            SecretKey secretKey=new SecretKeySpec(Base64.getDecoder().decode(key),"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            byte[] encrypted=cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch(Exception e){
            throw new RuntimeException("Encryption failed!");
        }
    }

    private String decrypt(String encrypted, String key){
        try{
            SecretKey secretKey=new SecretKeySpec(Base64.getDecoder().decode(key),"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] decrypted=cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decrypted);
        }
        catch(Exception e){
            throw new RuntimeException("Decryption failed!");
        }
    }


}
