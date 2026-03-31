package org.spring.steganography.Service;

import org.jspecify.annotations.Nullable;
import org.spring.steganography.DTO.UserDTO.UserResponse;
import org.spring.steganography.Exception.UserNotFoundException;
import org.spring.steganography.Model.*;
import org.spring.steganography.Repository.AuditLogRepo;
import org.spring.steganography.Repository.StegoRecordsRepo;
import org.spring.steganography.Repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepo userRepo;
    private final StegoRecordsRepo stegoRecordsRepo;
    private final AuditLogRepo auditLogRepo;

    public AdminService(UserRepo userRepo, StegoRecordsRepo stegoRecordsRepo, AuditLogRepo auditLogRepo) {
        this.userRepo = userRepo;
        this.stegoRecordsRepo = stegoRecordsRepo;
        this.auditLogRepo = auditLogRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findByRoleContaining(Role.USER);
    }

    public void deleteUser(String id) {
        User user=userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not found!"));
        userRepo.delete(user);
    }

    public User getById(String id) {
        return userRepo.findByIdAndRole(id,Role.USER).orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public @Nullable Long getUserCount() {
        return userRepo.countByRoleContaining(Role.USER);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public void toggleUserBan(String id) {
        User user=getById(id);
        user.setBanned(!user.isBanned());
        userRepo.save(user);
    }

    public Page<UserResponse> getUsersPaginated(int page, int size) {
        Page<User> users=userRepo.findByRoleContaining(Role.USER,PageRequest.of(page,size, Sort.by("createdAt").descending()));
        return users.map(user -> UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build());
    }

    public Map<String, Object> getSystemStats() {
        long userCount=userRepo.countByRoleContaining(Role.USER);
        long stegoRecords=stegoRecordsRepo.count();
        Map<String,Object> stats=new HashMap<>();
        stats.put("totalUsers",userCount);
            stats.put("totalStegoRecords",stegoRecords);
        return stats;
    }

    public List<StegoRecords> getAllStegoRecords() {
        return stegoRecordsRepo.findAll();
    }

    public List<AuditLog> getLogs() {
        return auditLogRepo.findAll();
    }
}
