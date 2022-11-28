package com.tweats.controller;

import com.tweats.exceptions.UserNotFoundException;
import com.tweats.service.UserPrincipalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private UserPrincipalService userPrincipalService;

    public UserController(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @GetMapping("/login")
    public Map<String, Object> login(Principal principal) throws UserNotFoundException {
        String email = principal.getName();
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("email", email);
        userDetails.put("role", userPrincipalService.getRoleName(email));
        return userDetails;
    }

}
