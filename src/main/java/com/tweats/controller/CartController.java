package com.tweats.controller;

import com.tweats.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    CartService cartService;

    @PostMapping
    public void add(Principal principal, @RequestParam long itemId, @RequestParam long quantity) {
        String userEmail = principal.getName();
        cartService.addItem(userEmail, itemId, quantity);
    }
}
