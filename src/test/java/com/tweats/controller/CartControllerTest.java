package com.tweats.controller;

import com.tweats.exceptions.ItemDoesNotExistException;
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
    void shouldBeAbleToAddItemToCart() throws ItemDoesNotExistException {
        long itemId = 1L;
        long quantity = 2L;
        String userEmail = "abc@gmail.com";
        when(principal.getName()).thenReturn(userEmail);

        cartController.add(principal, itemId, quantity);

        verify(cartService).addItem(userEmail, itemId, quantity);
    }
}
