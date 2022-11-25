package com.tweats.controller;

import com.tweats.exceptions.CartItemNotFoundException;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static org.mockito.Mockito.*;

public class CartControllerTest {

    private CartController cartController;
    private Principal principal;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        cartService = mock(CartService.class);
        cartController = new CartController(cartService);
    }

    @Test
    void shouldBeAbleToAddItemToCart() throws ItemDoesNotExistException, NoCategoryFoundException {
        long itemId = 1L;
        long quantity = 2L;
        String userEmail = "abc@gmail.com";
        when(principal.getName()).thenReturn(userEmail);

        cartController.add(principal, itemId, quantity);

        verify(cartService).addItem(userEmail, itemId, quantity);
    }

    @Test
    void shouldBeAbleToGetCartItemsWhenCategoryIdIsGiven() throws NoCategoryFoundException {
        long categoryId = 1L;
        String userEmail = "abc@gmail.com";
        when(principal.getName()).thenReturn(userEmail);

        cartController.getCartItems(principal, categoryId);

        verify(cartService).cartItems(userEmail, categoryId);
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfCartItemWhenQuantityIsGiven() throws CartItemNotFoundException {
        long cartItemId = 2L;
        long quantity = 30L;

        cartController.updateQuantity(cartItemId, quantity);

        verify(cartService).updateCartItemQuantity(cartItemId, quantity);
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenIdIsGiven() throws CartItemNotFoundException {
        long cartItemId = 2l;

        cartController.deleteCartItem(cartItemId);

        verify(cartService).deleteCartItem(cartItemId);
    }
}
