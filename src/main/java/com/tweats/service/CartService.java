package com.tweats.service;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.CartItemNotFoundException;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Cart;
import com.tweats.model.CartItem;
import com.tweats.model.Item;
import com.tweats.model.User;
import com.tweats.repo.CartItemRepository;
import com.tweats.repo.CartRepository;
import com.tweats.repo.ItemRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Setter
public class CartService {

    UserPrincipalService userPrincipalService;

    ItemRepository itemRepository;

    CartRepository cartRepository;

    CartItemRepository cartItemRepository;

    @Value("${application.link}")
    private String appLink;

    public CartService(UserPrincipalService userPrincipalService, ItemRepository itemRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.userPrincipalService = userPrincipalService;
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public void addItem(String userEmail, long itemId, long quantity) throws ItemDoesNotExistException, NoCategoryFoundException, UserNotFoundException {
        User user = getUserByEmail(userEmail);
        long userId = user.getId();
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new ItemDoesNotExistException();
        Item item = optionalItem.get();
        long categoryId = item.getCategory().getId();
        Cart cart = getCart(userId, categoryId);
        CartItem cartItem = cart.getCartItem(item, quantity);
        cart.addCartItem(cartItem);
        cartRepository.save(cart);
    }

    public CartResponse cartItems(String email, long categoryId) throws NoCategoryFoundException, UserNotFoundException {
        User user = getUserByEmail(email);
        long userId = user.getId();
        Cart cart = getCart(userId, categoryId);
        return getCartResponse(cart);
    }

    public void updateCartItemQuantity(long cartItemId, long quantity) throws CartItemNotFoundException {
        CartItem cartItem = getCartItem(cartItemId);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(long cartItemId) throws CartItemNotFoundException {
        getCartItem(cartItemId);
        cartItemRepository.deleteById(cartItemId);
    }

    private User getUserByEmail(String userEmail) throws UserNotFoundException {
        return userPrincipalService.findUserByEmail(userEmail);
    }

    public Cart getCart(long userId, long categoryId) throws NoCategoryFoundException {
        return cartRepository.findCartByUserIdAndCategoryId(userId, categoryId).orElseThrow(NoCategoryFoundException::new);
    }

    private String getImageLink(CartItem cartItem) {
        return appLink + "/images/" + (cartItem.getItem().getId());
    }

    private CartResponse getCartResponse(Cart cart) {
        Set<CartItem> cartItems = cart.getCartItems();
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), cartItem.getItem().getName(), cartItem.getQuantity(), cartItem.getItem().getPrice(), getImageLink(cartItem), cartItem.getItem().is_available());
            cartItemResponseList.add(cartItemResponse);
        }
        return new CartResponse(cart.getId(), cartRepository.getBillAmount(cart.getId()), cartItemResponseList);
    }

    private CartItem getCartItem(long cartItemId) throws CartItemNotFoundException {
        return cartItemRepository.findById(cartItemId).orElseThrow(CartItemNotFoundException::new);
    }

    public void emptyCart(long cartId) {

    }
}
