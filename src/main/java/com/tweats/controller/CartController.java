package com.tweats.controller;

import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.security.Principal;

@RestController
@Validated
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    CartService cartService;

    @PostMapping
    public void add(Principal principal,
                    @RequestParam long itemId,
                    @Min(value = 0, message = "quantity cannot be negative") @RequestParam long quantity) throws ItemDoesNotExistException {
        String userEmail = principal.getName();
        cartService.addItem(userEmail, itemId, quantity);
    }
}
