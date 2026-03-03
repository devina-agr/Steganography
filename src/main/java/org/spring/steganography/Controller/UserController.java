package org.spring.steganography.Controller;

import org.jspecify.annotations.Nullable;
import org.spring.steganography.DTO.UserDTO.UserResponse;
import org.spring.steganography.Model.User;
import org.spring.steganography.Security.UserPrincipal;
import org.spring.steganography.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal){
        User user=userService.getById(principal.getUserId());
        return ResponseEntity.ok(mapToResponse(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
        User user=userService.getById(id);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> usersList=userService
                .getAllUsers()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(usersList);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("#id==authentication.principal.userId")
    public ResponseEntity<String> changePassword(@PathVariable String id, @RequestParam String newPassword){
        userService.changePassword(id,newPassword);
        return ResponseEntity.ok("Password updated successfully!");
    }


    private @Nullable UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRole().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }



}
