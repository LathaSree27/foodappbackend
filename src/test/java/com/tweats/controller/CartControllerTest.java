package com.tweats.controller;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.CartItemNotFoundException;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;

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
        String userEmail = "abc@gmail.com";
        when(principal.getName()).thenReturn(userEmail);

        cartController.add(principal, itemId, quantity);

        verify(cartService).addItem(userEmail, itemId, quantity);
    }

    @Test
    void shouldBeAbleToGetCartItemsWhenCategoryIdIsGiven() throws NoCategoryFoundException, UserNotFoundException {
        String appLink = "http://localhost:8080/tweats/api/v1";
        long categoryId = 1;
        String userEmail = "abc@gmail.com";
        CartItemResponse mango = CartItemResponse.builder().id(1).name("Mango").quantity(2).price(new BigDecimal(50)).imageLink(appLink + "/images/" + 1).build();
        ArrayList<CartItemResponse> cartItemResponses = new ArrayList<>();
        cartItemResponses.add(mango);
        CartResponse cartResponse = CartResponse.builder().id(2).billAmount(new BigDecimal(100)).cartItemResponses(cartItemResponses).build();
        when(principal.getName()).thenReturn(userEmail);
        when(cartService.cartItems(userEmail, 1)).thenReturn(cartResponse);

        CartResponse actualCartItemsResponse = cartController.getCartItems(principal, categoryId);

        verify(cartService).cartItems(userEmail, categoryId);
        assertThat(actualCartItemsResponse, is(cartResponse));
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfCartItemWhenQuantityIsGiven() throws CartItemNotFoundException {
        long cartItemId = 2;
        long quantity = 30;

        cartController.updateQuantity(cartItemId, quantity);

        verify(cartService).updateCartItemQuantity(cartItemId, quantity);
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenIdIsGiven() throws CartItemNotFoundException {
        long cartItemId = 2;

        cartController.deleteCartItem(cartItemId);

        verify(cartService).deleteCartItem(cartItemId);
    }
}
