package org.spring.steganography.Controller;

import org.spring.steganography.DTO.StegoDTO.StegoResponse;
import org.spring.steganography.Model.StegoRecords;
import org.spring.steganography.Repository.StegoRecordsRepo;
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
    private final StegoRecordsRepo stegoRecordsRepo;

    public StegoController(StegoService stegoService, StegoRecordsRepo stegoRecordsRepo) {
        this.stegoService = stegoService;
        this.stegoRecordsRepo = stegoRecordsRepo;
    }

    @PostMapping("/encode")
    public ResponseEntity<StegoResponse> encode(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam("image") MultipartFile image, @RequestParam("secretText") String secretText){
        return ResponseEntity.ok(stegoService.encodeMessage(userPrincipal.getUserId(),image,secretText));
    }

    @PostMapping("/decode")
    public ResponseEntity<String> decode(@AuthenticationPrincipal UserPrincipal userPrincipal,@RequestParam("recordId") String recordId, @RequestParam("image") MultipartFile image, @RequestParam("secretKey") String secretKey){
        return ResponseEntity.ok(stegoService.decodeMessage(userPrincipal.getUserId(),recordId,image,secretKey));
    }

    @GetMapping("/records")
    public ResponseEntity<Page<StegoResponse>> getRecords(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam int page, @RequestParam int size){
        return ResponseEntity.ok(stegoService.getUserRecords(userPrincipal.getUserId(),page,size));
    }

    @GetMapping("/view/{recordId}")
    public ResponseEntity<String> viewImage(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String recordId){
        return ResponseEntity.ok(stegoService.getImageUrl(userPrincipal.getUserId(),recordId));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<String> deleteRecord(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String recordId, Principal principal){
        stegoService.deleteRecord(userPrincipal.getUserId(),recordId);
        return ResponseEntity.ok("Record deleted successfully!");
    }
}
