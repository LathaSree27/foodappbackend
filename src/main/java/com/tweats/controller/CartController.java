package com.tweats.controller;

import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
                    @Min(value = 0, message = "quantity cannot be negative") @RequestParam long quantity) throws ItemDoesNotExistException, NoCategoryFoundException {
        String userEmail = principal.getName();
        cartService.addItem(userEmail, itemId, quantity);
    }

    @GetMapping("{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCartItems(Principal principal,
                                     @PathVariable long categoryId) throws NoCategoryFoundException {
        String userEmail = principal.getName();
        return cartService.cartItems(userEmail, categoryId);
    }

    public void updateQuantity(long cartItemId, long quantity) {
        cartService.updateCartItemQuantity(cartItemId, quantity);
    }
}
