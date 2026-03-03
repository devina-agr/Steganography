package org.spring.steganography.Service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.spring.steganography.Model.User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    public User register(@NotBlank @Email String email, @NotBlank String password) {
    }

    public User getByEmail(@NotBlank @Email String email) {
    }

    public User getById(String id) {
    }

    public List<User> getAllUsers() {
    }

    public void deleteUser(String id) {
    }

    public void changePassword(String id, String newPassword) {
    }
}
