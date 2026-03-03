package org.spring.steganography.Controller;

import jakarta.validation.Valid;
import org.spring.steganography.DTO.UserDTO.AuthRequest;
import org.spring.steganography.DTO.UserDTO.AuthResponse;
import org.spring.steganography.Model.User;
import org.spring.steganography.Security.JWTServices;
import org.spring.steganography.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JWTServices jWTServices;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JWTServices jWTServices, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jWTServices = jWTServices;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest authRequest){
        User user=userService.register(authRequest.getEmail(),authRequest.getPassword());
        String token=jWTServices.generateToken(user.getEmail());
        AuthResponse response=new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().stream().map(Enum::name).toList()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        User user=userService.getByEmail(authRequest.getEmail());
        String token=jWTServices.generateToken(user.getEmail());
        AuthResponse response=new AuthResponse(token,user.getEmail(),user.getRole().stream().map(Enum::name).toList());
        return ResponseEntity.ok(response);
    }
}
