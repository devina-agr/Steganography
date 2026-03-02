package org.spring.steganography.Controller;

import org.spring.steganography.Model.User;
import org.spring.steganography.Security.UserPrincipal;
import org.spring.steganography.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

     public ResponseEntity<User> getAllUsers(@AuthenticationPrincipal UserPrincipal userPrincipal){
            return userService.getAllUsers(userPrincipal.getUserId());
     }

     

}
