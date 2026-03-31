package org.spring.steganography.Controller;

import org.spring.steganography.DTO.AdminDTO.AdminInviteDTO;
import org.spring.steganography.DTO.AdminDTO.AdminRegisterRequest;
import org.spring.steganography.DTO.UserDTO.AuthRequest;
import org.spring.steganography.DTO.UserDTO.ChangePasswordDTO.ChangePasswordRequest;
import org.spring.steganography.DTO.UserDTO.ChangePasswordDTO.ForgetPasswordRequest;
import org.spring.steganography.DTO.UserDTO.ChangePasswordDTO.ResetPasswordRequest;
import org.spring.steganography.DTO.UserDTO.EmailChangeRequest;
import org.spring.steganography.DTO.UserDTO.UserResponse;
import org.spring.steganography.Exception.InvalidInviteException;
import org.spring.steganography.Model.*;
import org.spring.steganography.Repository.UserRepo;
import org.spring.steganography.Security.UserPrincipal;
import org.spring.steganography.Service.AdminInviteService;
import org.spring.steganography.Service.AdminService;
import org.spring.steganography.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {


    private final AdminService adminService;
    private final AdminInviteService adminInviteService;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    public AdminController(AdminService adminService, AdminInviteService adminInviteService, UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, UserService userService) {
        this.adminService = adminService;
        this.adminInviteService = adminInviteService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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

    @PatchMapping("/users/{id}/ban")
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

    @GetMapping("/audit-logs")
    public List<AuditLog> getLogs(){
        return adminService.getLogs();
    }

    @PostMapping("/invite")
    public ResponseEntity<String> inviteAdmin(@AuthenticationPrincipal UserPrincipal userPrincipal,@RequestBody AdminInviteDTO adminInviteDTO){
        adminInviteService.sendInvite(userPrincipal.getUsername(),adminInviteDTO.getEmail());
        return ResponseEntity.ok("Invite sent!");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@RequestParam String token, @RequestBody AdminRegisterRequest request){
        AdminInvite adminInvite=adminInviteService.validateInvite(token);
        if(userRepo.findByEmail(adminInvite.getEmail()).isPresent()){
            throw new InvalidInviteException("User already exists");
        }
        if(request.getPassword().length()<8){
            throw new RuntimeException("Password too short!");
        }
        User user=new User();
        user.setEmail(adminInvite.getEmail());
        user.setRole(Set.of(Role.ADMIN));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);
        user.setTokenVersion(0);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);
        adminInviteService.markUsed(adminInvite);
        return ResponseEntity.ok("Admin registered successfully!");
    }


    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ChangePasswordRequest request){
        System.out.println("USER PRINCIPAL: " + userPrincipal);
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

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
