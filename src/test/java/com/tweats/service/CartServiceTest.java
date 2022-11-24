package com.tweats.service;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.model.*;
import com.tweats.repo.CartRepository;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void shouldBeAbleToSaveCartItemWhenUserAddsItemToCart() throws ItemDoesNotExistException {
        String userEmail = "abc@gmail.com";
        long itemId = 1L;
        long quantity = 2L;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item = new Item(itemName, image, price, category);
        CartItem cartItem = new CartItem(cart, item, quantity);
        Set<CartItem> expectedCartItems = new HashSet<>();
        expectedCartItems.add(cartItem);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.findCartByUserIdAndCategoryId(user.getId(), category.getId())).thenReturn(cart);

        cartService.addItem(userEmail, itemId, quantity);

        verify(cartRepository).save(cart);
        assertThat(cart.getCartItems(), is(expectedCartItems));
    }

    @Test
    void shouldThrowItemDoesNotExistExceptionWhenItemWithTheGivenIdDoesNotExist() {
        String userEmail = "abc@gmail.com";
        long itemId = 1L;
        long quantity = 2L;
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);

        assertThrows(ItemDoesNotExistException.class, () -> cartService.addItem(userEmail, itemId, quantity));
    }

    @Test
    void shouldBeAbleToGetCartItemsWhenUserEmailAndAValidCategoryIdIsGiven() {
        String appLink = "http://localhost:8080/tweats/api/v1";
        cartService.setAppLink(appLink);
        String userEmail = "abc@gamil.com";
        long categoryId = 1L;
        String firstItemName = "Mango";
        BigDecimal firstItemPrice = new BigDecimal(80);
        Image itemImage = mock(Image.class);
        Item mango = new Item(firstItemName, itemImage, firstItemPrice, category);
        HashSet<CartItem> cartItems = new HashSet<>();
        CartItem cartItem = new CartItem(cart, mango, 2l);
        cartItems.add(cartItem);
        cartItem.setId(2L);
        cart.setCartItems(cartItems);
        cart.setId(2L);
        BigDecimal billAmount = new BigDecimal(160);
        CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), cartItem.getItem().getName(), cartItem.getQuantity(), cartItem.getItem().getPrice(), appLink + "/images/" + (cartItem.getItem().getId()),cartItem.getItem().is_available());
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        cartItemResponseList.add(cartItemResponse);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(cartRepository.findCartByUserIdAndCategoryId(user.getId(), categoryId)).thenReturn(cart);
        when(cartRepository.getBillAmount(cart.getId())).thenReturn(billAmount);
        CartResponse expectedCartResponse = new CartResponse(cart.getId(), cartRepository.getBillAmount(cart.getId()), cartItemResponseList);

        CartResponse actualCartResponse = cartService.cartItems(userEmail, categoryId);

        assertThat(actualCartResponse, is(expectedCartResponse));
    }
}