package org.spring.steganography.Controller;

import org.spring.steganography.DTO.UserDTO.UserResponse;
import org.spring.steganography.Model.StegoRecords;
import org.spring.steganography.Model.User;
import org.spring.steganography.Service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
        User user=adminService.getById(id);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> usersList=adminService
                .getAllUsers()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(usersList);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount(){
        return ResponseEntity.ok(adminService.getUserCount());
    }

    @GetMapping("/users/search")
    public ResponseEntity<UserResponse> findUserByEmail(@RequestParam String email){
        User user=adminService.getUserByEmail(email);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @PatchMapping("/users/(id)/ban")
    public ResponseEntity<String> toggleUserBan(@PathVariable String id){
        adminService.toggleUserBan(id);
        return ResponseEntity.ok("User ban status updated!");
    }

    @GetMapping("/users/paginated")
    public ResponseEntity<Page<UserResponse>> getUsersPaginated(@RequestParam int page, @RequestParam int size){
         return ResponseEntity.ok(adminService.getUsersPaginated(page, size));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats(){
        return ResponseEntity.ok(adminService.getSystemStats());
    }

    @GetMapping("/stego-records")
    public ResponseEntity<List<StegoRecords>> getAllStegoRecords(){
        return ResponseEntity.ok(adminService.getAllStegoRecords());
    }


    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
