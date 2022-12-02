package com.tweats.controller;

import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.*;
import com.tweats.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;
    @Mock
    private Principal principal;

    @Mock
    private CartService cartService;

    @Test
    void shouldBeAbleToAddItemToCart() throws ItemDoesNotExistException, NoCategoryFoundException, UserNotFoundException {
        long itemId = 1;
        long quantity = 2;

        cartController.add(principal, itemId, quantity);

        verify(cartService).addItem(principal.getName(), itemId, quantity);
    }

    @Test
    void shouldBeAbleToGetCartItemsWhenCategoryIdIsGiven() throws NoCategoryFoundException, UserNotFoundException {
        long categoryId = 1;
        CartResponse expectedCartResponse = new CartResponse();
        when(cartService.getCartItems(principal.getName(), 1)).thenReturn(expectedCartResponse);

        CartResponse actualCartItemsResponse = cartController.getCartItems(principal, categoryId);

        verify(cartService).getCartItems(principal.getName(), categoryId);
        assertThat(actualCartItemsResponse, is(expectedCartResponse));
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfCartItemWhenQuantityIsGiven() throws CartItemNotFoundException, CartAccessDeniedException, CartNotFoundException {
        long cartId = 2;
        long itemId = 1;
        long quantity = 30;

        cartController.updateQuantity(principal, cartId, itemId, quantity);

        verify(cartService).updateCartItemQuantity(principal.getName(), cartId, itemId, quantity);
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenIdIsGiven() throws CartItemNotFoundException, CartNotFoundException {
        long cartId = 2;

        int itemId = 3;
        cartController.deleteCartItem(cartId, itemId);

        verify(cartService).deleteCartItem(cartId, itemId);
    }
}

