package org.spring.steganography.Controller;

import org.spring.steganography.DTO.StegoDTO.DecodeRequest;
import org.spring.steganography.DTO.StegoDTO.EncodeRequest;
import org.spring.steganography.DTO.StegoDTO.StegoResponse;
import org.spring.steganography.Security.UserPrincipal;
import org.spring.steganography.Service.StegoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/stego")
public class StegoController {

    private final StegoService stegoService;

    public StegoController(StegoService stegoService) {
        this.stegoService = stegoService;
    }

    @PostMapping("/encode")
    public ResponseEntity<StegoResponse> encode(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam MultipartFile image, @RequestBody EncodeRequest request){
        return ResponseEntity.ok(stegoService.encodeMessage(userPrincipal.getUserId(),image,request.getSecretText()));
    }

    @PostMapping("/decode")
    public ResponseEntity<String> decode(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam MultipartFile image, @RequestBody DecodeRequest request, Principal principal){
        return ResponseEntity.ok(stegoService.decodeMessage(userPrincipal.getUserId(),image,request));
    }

    @GetMapping("/records")
    public ResponseEntity<Page<StegoResponse>> getRecords(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam int page, @RequestParam int size){
        return ResponseEntity.ok(stegoService.getUserRecords(userPrincipal.getUserId(),page,size));
    }

    @GetMapping("/download/{recordId}")
    public ResponseEntity<byte[]> downloadImage(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String recordId){
        return ResponseEntity.ok(stegoService.downloadImage(userPrincipal.getUserId(),recordId));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<String> deleteRecord(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String recordId, Principal principal){
        stegoService.deleteRecord(userPrincipal.getUserId(),recordId);
        return ResponseEntity.ok("Record deleted successfully!");
    }
}
