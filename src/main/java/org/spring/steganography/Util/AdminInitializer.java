package org.spring.steganography.Util;

import org.spring.steganography.Model.Role;
import org.spring.steganography.Model.User;
import org.spring.steganography.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    public void run(String... args) throws Exception {
        if(userRepo.findByEmail("admin@stego.com").isEmpty()){
            User admin=new User();
            admin.setEmail("admin@stego.com");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setRole(Set.of(Role.ADMIN));
            admin.setEnabled(true);
            admin.setTokenVersion(0);
            admin.setCreatedAt(LocalDateTime.now());
            userRepo.save(admin);
        }
    }
}
