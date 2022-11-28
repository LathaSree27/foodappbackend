package com.tweats.service;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.CartItemNotFoundException;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.*;
import com.tweats.repo.CartItemRepository;
import com.tweats.repo.CartRepository;
import com.tweats.repo.ItemRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;
    @Mock
    private UserPrincipalService userPrincipalService;
    @Mock
    private ItemRepository itemRepository;
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
        String userEmail = "abc@gmail.com";
        long itemId = 1;
        long quantity = 2;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item = new Item(itemName, image, price, category);
        CartItem cartItem = new CartItem(cart, item, quantity);
        Set<CartItem> expectedCartItems = new HashSet<>();
        expectedCartItems.add(cartItem);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.findCartByUserIdAndCategoryId(user.getId(), category.getId())).thenReturn(Optional.of(cart));

        cartService.addItem(userEmail, itemId, quantity);

        verify(cartRepository).save(cart);
        assertThat(cart.getCartItems(), is(expectedCartItems));
    }

    @Test
    void shouldThrowItemDoesNotExistExceptionWhenItemWithTheGivenIdDoesNotExist() throws UserNotFoundException {
        String userEmail = "abc@gmail.com";
        long itemId = 1;
        long quantity = 2;
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);

        assertThrows(ItemDoesNotExistException.class, () -> cartService.addItem(userEmail, itemId, quantity));
    }

    @Test
    void shouldBeAbleToGetCartItemsWhenUserEmailAndAValidCategoryIdIsGiven() throws NoCategoryFoundException, UserNotFoundException {
        String appLink = "http://localhost:8080/tweats/api/v1";
        cartService.setAppLink(appLink);
        String userEmail = "abc@gamil.com";
        long categoryId = 1;
        String firstItemName = "Mango";
        BigDecimal firstItemPrice = new BigDecimal(80);
        Image itemImage = mock(Image.class);
        Item mango = new Item(firstItemName, itemImage, firstItemPrice, category);
        HashSet<CartItem> cartItems = new HashSet<>();
        long quantity = 2;
        CartItem cartItem = new CartItem(cart, mango, quantity);
        cartItems.add(cartItem);
        cartItem.setId(2);
        cart.setCartItems(cartItems);
        cart.setId(2);
        BigDecimal billAmount = new BigDecimal(160);
        CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), cartItem.getItem().getName(), cartItem.getQuantity(), cartItem.getItem().getPrice(), appLink + "/images/" + (cartItem.getItem().getId()), cartItem.getItem().isAvailable());
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        cartItemResponseList.add(cartItemResponse);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(cartRepository.findCartByUserIdAndCategoryId(user.getId(), categoryId)).thenReturn(Optional.of(cart));
        when(cartRepository.getBillAmount(cart.getId())).thenReturn(billAmount);
        CartResponse expectedCartResponse = new CartResponse(cart.getId(), cartRepository.getBillAmount(cart.getId()), cartItemResponseList);

        CartResponse actualCartResponse = cartService.cartItems(userEmail, categoryId);

        assertThat(actualCartResponse, is(expectedCartResponse));
    }

    @Test
    void shouldThrowNoCategoryFoundExceptionWhenCategoryDoesNotExistsWithGivenId() throws UserNotFoundException {
        String userEmail = "abc@gmail.com";
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        long categoryId = 2;
        assertThrows(NoCategoryFoundException.class, () -> cartService.cartItems(userEmail, categoryId));
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfACartItemWhenCartItemIdAndQuantityAreGiven() throws CartItemNotFoundException {
        long cartItemId = 2;
        long quantity = 5;
        long initialQuantity = 2;
        CartItem cartItem = new CartItem(cart, item, initialQuantity);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        cartService.updateCartItemQuantity(cartItemId, quantity);

        assertThat(cartItem.getQuantity(), is(quantity));
    }

    @Test
    void shouldThrowCartItemNotFoundExceptionWhenCartItemDoesNotExistsWithGivenId() {
        long cartItemId = 2;
        long quantity = 4;

        assertThrows(CartItemNotFoundException.class, () -> cartService.updateCartItemQuantity(cartItemId, quantity));
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
