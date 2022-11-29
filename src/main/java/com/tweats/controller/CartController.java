package com.tweats.controller;

import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.CartItemNotFoundException;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.service.CartService;
import lombok.AllArgsConstructor;
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

    @PostMapping("item/{itemId}")
    public void add(Principal principal,
                    @PathVariable long itemId,
                    @Min(value = 0, message = "quantity cannot be negative")
                    @RequestParam long quantity) throws ItemDoesNotExistException, NoCategoryFoundException, UserNotFoundException {
        cartService.addItem(principal.getName(), itemId, quantity);
    }

    @GetMapping("category/{categoryId}")
    public CartResponse getCartItems(Principal principal,
                                     @PathVariable long categoryId) throws NoCategoryFoundException, UserNotFoundException {
        return cartService.getCartItems(principal.getName(), categoryId);
    }

    @PutMapping("item/{cartItemId}")
    public void updateQuantity(@PathVariable long cartItemId,
                               @RequestParam(value = "quantity") @Min(value = 0, message = "Quantity can't be negative!") long quantity) throws CartItemNotFoundException {
        cartService.updateCartItemQuantity(cartItemId, quantity);
    }

    @DeleteMapping("item/{cartItemId}")
    public void deleteCartItem(@PathVariable long cartItemId) throws CartItemNotFoundException {
        cartService.deleteCartItem(cartItemId);
    }
}
