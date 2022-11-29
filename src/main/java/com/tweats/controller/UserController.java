package com.tweats.controller;

import com.tweats.controller.response.LoginResponse;
import com.tweats.service.UserPrincipalService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class UserController {
    private UserPrincipalService userPrincipalService;

    @GetMapping("/login")
    public LoginResponse login(Principal principal) {
        return userPrincipalService.getLoginResponse(principal.getName());
    }
}
