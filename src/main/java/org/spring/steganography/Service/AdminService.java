package org.spring.steganography.Service;

import org.jspecify.annotations.Nullable;
import org.spring.steganography.Exception.UserNotFoundException;
import org.spring.steganography.Model.User;
import org.spring.steganography.Repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepo userRepo;

    public AdminService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void deleteUser(String id) {
        User user=userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not found!"));
        userRepo.delete(user);
    }

    public User getById(String id) {
        return userRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public @Nullable Long getUserCount() {
    }

    public User getUserByEmail(String email) {
    }

    public void toggleUserBan(String id) {
    }
}
