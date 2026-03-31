package org.spring.steganography.Controller;

import org.jspecify.annotations.Nullable;
import org.spring.steganography.DTO.UserDTO.ChangePasswordDTO.ChangePasswordRequest;
import org.spring.steganography.DTO.UserDTO.ChangePasswordDTO.ForgetPasswordRequest;
import org.spring.steganography.DTO.UserDTO.ChangePasswordDTO.ResetPasswordRequest;
import org.spring.steganography.DTO.UserDTO.EmailChangeRequest;
import org.spring.steganography.DTO.UserDTO.UserResponse;
import org.spring.steganography.Model.User;
import org.spring.steganography.Security.UserPrincipal;
import org.spring.steganography.Service.AdminService;
import org.spring.steganography.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER')")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final AdminService adminService;
    private UserService userService;

    public UserController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal){
        User user=adminService.getById(principal.getUserId());
        return ResponseEntity.ok(mapToResponse(user));
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ChangePasswordRequest request){
        userService.changePassword(userPrincipal.getUsername(),request.getOldPassword(),request.getNewPassword());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Password updated successfully! Please login again.");
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordRequest request){
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password resent link sent to your email.");
    }



    @PostMapping("/email/request")
    public ResponseEntity<String> requestEmailChange(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody EmailChangeRequest emailChangeRequest){
            userService.requestEmailChange(userPrincipal.getUserId(),emailChangeRequest.getNewEmail(),emailChangeRequest.getPassword());
            return ResponseEntity.ok("Verification link sent to your new email.");
    }

    @GetMapping("/email/confirm")
    public ResponseEntity<String> confirmEmailChange(@RequestParam String token){
        userService.confirmEmailChange(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Email updated successfully. Please login again!");
    }



    private @Nullable UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
