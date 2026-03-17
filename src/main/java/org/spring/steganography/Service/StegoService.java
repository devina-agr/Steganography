package org.spring.steganography.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.constraints.NotBlank;
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
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class StegoService {

    private final StegoRecordsRepo stegoRecordsRepo;
    private final Cloudinary cloudinary;

    public StegoService(StegoRecordsRepo stegoRecordsRepo, Cloudinary cloudinary) {
        this.stegoRecordsRepo = stegoRecordsRepo;
        this.cloudinary = cloudinary;
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
        try{
            Map uploadResult=cloudinary.uploader().upload(encodedImage, ObjectUtils.asMap(
                    "folder","stego",
                    "resource_type","image"
            ));
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed",e);
        }
    }

    private byte[] hideMessage(MultipartFile image, String encryptedMessage) {
        try{
            BufferedImage img= ImageIO.read(image.getInputStream());
            if(img==null){
                throw new RuntimeException("Unsupported image format. Please upload PNG or JPG");
            }
            byte[] msgBytes=encryptedMessage.getBytes(StandardCharsets.UTF_8);
            int msgLength=msgBytes.length;
            int totalBits=32+(msgLength*8);
            int width=img.getWidth();
            int height=img.getHeight();
            int capacity=width*height*3;
            if(totalBits>capacity){
                throw new RuntimeException("Message too large for this image!");
            }
            int bitIndex=0;
            for(int y=0;y<height;y++){
                for(int x=0;x<width;x++){
                    int pixel=img.getRGB(x,y);
                    int r=(pixel>>16) & 0xff;
                    int g=(pixel>>8) & 0xff;
                    int b=pixel & 0xff;
                    int[] channels={r,g,b};
                    for(int i=0;i<channels.length && bitIndex<totalBits;i++){
                        channels[i]=setLSB(channels[i], getBit(msgLength,msgBytes,bitIndex++));
                    }
                    r=channels[0];
                    g=channels[1];
                    b=channels[2];

                    int alpha=(pixel>>24) & 0xff;
                    int newPixel=(alpha<<24) | (r<<16) | (g<<8) | b;
                    img.setRGB(x,y,newPixel);
                    if(bitIndex>=totalBits){
                        break;
                    }
                }
                if(bitIndex>=totalBits){
                    break;
                }
            }
            File temp=File.createTempFile("stego",".png");
            ImageIO.write(img,"png",temp);
            return Files.readAllBytes(temp.toPath());
        } catch (IOException e) {
             throw new RuntimeException("Steganography encoding failed! ",e);
        }
    }

    private int setLSB(int value, int bit) {
        return (value & 0xFE) | bit;
    }

    private int getBit(int msgLength, byte[] msgBytes, int index) {
            if(index<32){
                return (msgLength>>(31-index)) & 1;
            }
            int byteIndex=(index-32)/8;
            int bitIndex=(index-32)%8;
            return (msgBytes[byteIndex]>>(7-bitIndex))&1;
    }

    public String decodeMessage(String userId, String recordId, MultipartFile image, String secretKey) {
        StegoRecords records=stegoRecordsRepo.findById(recordId).orElseThrow(()->new UserNotFoundException("Records not found!"));
        if(!records.getUserId().equals(userId)){
            throw new UnAuthorizedActionException("Access Denied!");
        }
        validateImage(image);
        if(!SecurityUtils.hashToken(secretKey).equals(records.getSecretKeyHash())){
            throw new UnAuthorizedActionException("Invalid secret key!");
        }
        String hiddenMessage=extractMessage(image);
        return decrypt(hiddenMessage,secretKey);
    }

    private String extractMessage(MultipartFile image) {
        try{
            BufferedImage img=ImageIO.read(image.getInputStream());
            if(img==null){
                throw new RuntimeException("Invalid image!");
            }
            int width= img.getWidth();
            int height= img.getHeight();
            int bitIndex=0;
            int msgLength=0;
            byte[] messageBytes=null;
            for(int y=0;y<height;y++){
                for(int x=0;x<width;x++){
                    int pixel=img.getRGB(x,y);
                    int r=(pixel>>16)&1;
                    int g=(pixel>>8)&1;
                    int b=pixel&1;
                    int[] bits={r,g,b};
                    for(int bit:bits){
                        if(bitIndex<32){
                            msgLength=(msgLength<<1)|bit;
                        }
                        if(bitIndex==31){
                            messageBytes=new byte[msgLength];
                        }
                        else if(messageBytes!=null){
                            int byteIndex=(bitIndex-32)/8;
                            int bitPos=7-((bitIndex-32)%8);
                            if(byteIndex<messageBytes.length){
                                messageBytes[byteIndex]|=bit<<bitPos;
                            }
                        }
                        bitIndex++;
                        if(messageBytes!=null && bitIndex>=32 + messageBytes.length*8){
                            return new String(messageBytes,StandardCharsets.UTF_8);
                        }
                    }
                }
            }
            throw new RuntimeException("No hidden message found!");

        }
        catch (IOException e){
            throw new RuntimeException("Failed to decode message ",e);
        }
    }

    public Page<StegoResponse> getUserRecords(String userId, int page, int size) {
        return stegoRecordsRepo.findByUserId(userId, PageRequest.of(page,size))
                               .map(record->new StegoResponse(record.getId(),record.getEncodedImageUrl(),null));
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
            Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            byte[] encrypted=cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch(Exception e){
            throw new RuntimeException("Encryption failed!",e);
        }
    }

    private String decrypt(String encrypted, String key){
        try{
            SecretKey secretKey=new SecretKeySpec(Base64.getDecoder().decode(key),"AES");
            Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] decrypted=cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decrypted);
        }
        catch(Exception e){
            throw new RuntimeException("Decryption failed!",e);
        }
    }


    public String getImageUrl(String userId, String recordId) {
        StegoRecords records=stegoRecordsRepo.findById(recordId).orElseThrow(()->new UserNotFoundException("Records not found!"));
        if(!records.getUserId().equals(userId)){
            throw new UnAuthorizedActionException("Access denied!");
        }
        return records.getEncodedImgUrl();
    }
}
