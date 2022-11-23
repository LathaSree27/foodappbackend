package com.tweats.service;

import com.tweats.model.*;
import com.tweats.repo.CartRepository;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    private CartService cartService;

    private UserPrincipalService userPrincipalService;
    private ItemRepository itemRepository;
    private CartRepository cartRepository;
    private User user;
    private Category category;
    private Cart cart;
    private Image image;

    @BeforeEach
    void setUp() {
        userPrincipalService = mock(UserPrincipalService.class);
        itemRepository = mock(ItemRepository.class);
        cartRepository = mock(CartRepository.class);
        cartService = new CartService(userPrincipalService, itemRepository, cartRepository);
        user = mock(User.class);
        category = mock(Category.class);
        cart = new Cart(category, user);
        image = mock(Image.class);
    }

    @Test
    void shouldBeAbleToSaveCartItem() {
        String userEmail = "abc@gmail.com";
        long itemId = 1L;
        long quantity = 2L;
        String item1Name = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item1 = new Item(item1Name, image, price, category);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(cartRepository.findCartByUserIdAndCategoryId(user.getId(), category.getId())).thenReturn(cart);
        CartItem cartItem = new CartItem(cart, item1, quantity);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);

        cartService.addItem(userEmail, itemId, quantity);

        verify(cartRepository).save(cart);
        assertThat(cart.getCartItems(), is(cartItems));
    }
}
