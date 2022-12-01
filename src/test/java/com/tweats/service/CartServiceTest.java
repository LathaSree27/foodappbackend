package com.tweats.service;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.*;
import com.tweats.model.*;
import com.tweats.repo.CartItemRepository;
import com.tweats.repo.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;
    @Mock
    private UserPrincipalService userPrincipalService;
    @Mock
    private ItemService itemService;
    @Mock
    private ImageService imageService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private User user;
    @Mock
    private Category category;
    @Mock
    CartItem cartItem;
    private Cart cart;
    @Mock
    private Image image;
    @Mock
    Item item;
    @Mock
    private CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        cart = new Cart(category, user);
    }

    @Test
    void shouldBeAbleToSaveCartItemWhenUserAddsItemToCart() throws ItemDoesNotExistException, NoCategoryFoundException, UserNotFoundException {
        long itemId = 1;
        long quantity = 2;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item = new Item(itemName, image, price, category);
        CartItem cartItem = new CartItem(cart, item, quantity);
        Set<CartItem> expectedCartItems = new HashSet<>();
        expectedCartItems.add(cartItem);
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(itemService.getItem(itemId)).thenReturn(item);
        when(cartRepository.findByUserIdAndCategoryId(user.getId(), category.getId())).thenReturn(Optional.of(cart));

        cartService.addItem(user.getEmail(), itemId, quantity);

        verify(cartRepository).save(cart);
        assertThat(cart.getCartItems(), is(expectedCartItems));
    }

    @Test
    void shouldBeAbleToGetCartItemsWhenUserEmailAndAValidCategoryIdIsGiven() throws NoCategoryFoundException, UserNotFoundException {
        String imageLink = "http://localhost:8080/tweats/api/v1/images/" + image.getId();
        long categoryId = 1;
        String firstItemName = "Mango";
        BigDecimal firstItemPrice = new BigDecimal(80);
        Item mango = new Item(firstItemName, image, firstItemPrice, category);
        long quantity = 2;
        CartItem cartItem = new CartItem(cart, mango, quantity);
        cart.addCartItem(cartItem);
        BigDecimal billAmount = new BigDecimal(160);
        CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), mango.getName(), cartItem.getQuantity(), mango.getPrice(), imageLink, mango.isAvailable());
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        cartItemResponseList.add(cartItemResponse);
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(imageService.getImageLink(image)).thenReturn(imageLink);
        when(cartRepository.findByUserIdAndCategoryId(user.getId(), categoryId)).thenReturn(Optional.of(cart));
        when(cartRepository.getBillAmount(cart.getId())).thenReturn(Optional.of(billAmount));
        CartResponse expectedCartResponse = new CartResponse(cart.getId(), billAmount, cartItemResponseList);

        CartResponse actualCartResponse = cartService.getCartItems(user.getEmail(), categoryId);

        assertThat(actualCartResponse, is(expectedCartResponse));
    }

    @Test
    void shouldThrowNoCategoryFoundExceptionWhenCategoryDoesNotExistsWithGivenId() throws UserNotFoundException {
        long categoryId = 2;
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(NoCategoryFoundException.class, () -> cartService.getCartItems(user.getEmail(), categoryId));
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfACartItemWhenCartItemIdAndQuantityAreGiven() throws CartItemNotFoundException, CartAccessDeniedException, CartNotFoundException {
        long itemId = 2;
        long cartId = 1;
        long initialQuantity = 2;
        long quantity = 5;
        String email = "abc@gmail.com";
        CartItem cartItem = new CartItem(cart, item, initialQuantity);
        cart.addCartItem(cartItem);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(user.getEmail()).thenReturn(email);
        when(item.getId()).thenReturn(itemId);

        cartService.updateCartItemQuantity(email, cartId, itemId, quantity);

        assertThat(cartItem.getQuantity(), is(quantity));
    }

    @Test
    void shouldThrowCartItemNotFoundExceptionWhenCartItemDoesNotExistsWithGivenItem() {
        long itemId = 2;
        long cartId = 1;
        long quantity = 4;
        String email = "abc@gmail.com";
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(user.getEmail()).thenReturn(email);

        assertThrows(CartItemNotFoundException.class, () -> cartService.updateCartItemQuantity(email, cartId, itemId, quantity));
    }

    @Test
    void shouldThrowCartAccessDeniedExceptionWhenGivenCartDoesNotBelongsToUser() {
        long itemId = 2;
        long cartId = 1;
        long quantity = 4;
        String email = "abc@gmail.com";
        String cartUserEmail = "xyz@gmail.com";
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(user.getEmail()).thenReturn(cartUserEmail);

        assertThrows(CartAccessDeniedException.class, () -> cartService.updateCartItemQuantity(email, cartId, itemId, quantity));
    }

    @Test
    void shouldThrowCartNotFoundExceptionWhenCartDoesNotFoundWithGivenId() {
        long itemId = 2;
        long cartId = 1;
        long quantity = 4;
        String email = "abc@gmail.com";

        assertThrows(CartNotFoundException.class, () -> cartService.updateCartItemQuantity(email, cartId, itemId, quantity));
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenIdIsGiven() throws CartItemNotFoundException {
        long cartItemId = 2;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        cartService.deleteCartItem(cartItemId);

        verify(cartItemRepository).deleteById(cartItemId);
    }

    @Test
    void shouldThrowCartItemNotFoundExceptionWhenCartItemDoesNotExistWithGivenId() {
        long cartItemId = 2;

        assertThrows(CartItemNotFoundException.class, () -> cartService.deleteCartItem(cartItemId));
    }

    @Test
    void shouldBeAbleToEmptyCartWhenCartIsGiven() {
        long quantity = 1;
        Cart savedCart = new Cart(category, user);
        savedCart.addCartItem(new CartItem(savedCart, item, quantity));

        cartService.emptyCart(savedCart);

        assertThat(savedCart.getCartItems(), is(empty()));
        verify(cartRepository).save(savedCart);
    }
}
