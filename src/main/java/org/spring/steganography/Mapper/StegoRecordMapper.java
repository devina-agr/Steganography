package org.spring.steganography.Mapper;

import org.spring.steganography.Model.StegoRecords;

import java.time.LocalDateTime;

public class StegoRecordMapper {

    public static StegoRecords createRecords(
            String userId,
            String imgName,
            String encodedImgUrl,
            LocalDateTime createdAt,
            String secretKeyHash,
            long imgSize
    ){
        return StegoRecords.builder()
                .userId(userId)
                .imgName(imgName)
                .encodedImgUrl(encodedImgUrl)
                .imgSize(imgSize)
                .createdAt(createdAt)
                .secretKeyHash(secretKeyHash)
                .build();

    }



}
