package com.tweats.service;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Cart;
import com.tweats.model.CartItem;
import com.tweats.model.Item;
import com.tweats.model.User;
import com.tweats.repo.CartItemRepository;
import com.tweats.repo.CartRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Setter
@AllArgsConstructor
public class CartService {

    private UserPrincipalService userPrincipalService;

    private ItemService itemService;

    private CartRepository cartRepository;

    private CartItemRepository cartItemRepository;

    private ImageService imageService;

    public void addItem(String userEmail, long itemId, long quantity) throws ItemDoesNotExistException, NoCategoryFoundException, UserNotFoundException {
        User user = userPrincipalService.findUserByEmail(userEmail);
        Item item = itemService.getItem(itemId);
        Cart cart = getCart(user.getId(), item.getCategory().getId());
        CartItem cartItem = cart.getCartItem(item, quantity);
        cart.addCartItem(cartItem);
        cartRepository.save(cart);
    }

    public CartResponse getCartItems(String email, long categoryId) throws NoCategoryFoundException, UserNotFoundException {
        User user = userPrincipalService.findUserByEmail(email);
        Cart cart = getCart(user.getId(), categoryId);
        return getCartResponse(cart);
    }

    public void updateCartItemQuantity(String email, long cartId, long itemId, long quantity) throws CartItemNotFoundException, CartAccessDeniedException {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Cart cart = optionalCart.get();
        String cartUserEmail = cart.getUser().getEmail();
        if (!cartUserEmail.equals(email)) throw new CartAccessDeniedException();
        Set<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = getAvailableCartItem(itemId, cartItems);
        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
    }

    private CartItem getAvailableCartItem(long itemId, Set<CartItem> cartItems) throws CartItemNotFoundException {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getId() == itemId) return cartItem;
        }
        throw new CartItemNotFoundException();
    }

    public void deleteCartItem(long cartItemId) throws CartItemNotFoundException {
        getCartItem(cartItemId);
        cartItemRepository.deleteById(cartItemId);
    }

    public Cart getCart(long userId, long categoryId) throws NoCategoryFoundException {
        return cartRepository.findByUserIdAndCategoryId(userId, categoryId).orElseThrow(NoCategoryFoundException::new);
    }

    public void emptyCart(Cart cart) {
        cart.emptyCart();
        cartRepository.save(cart);
    }

    private CartResponse getCartResponse(Cart cart) {
        Set<CartItem> cartItems = cart.getCartItems();
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Item item = cartItem.getItem();
            CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), item.getName(), cartItem.getQuantity(), item.getPrice(), imageService.getImageLink(item.getImage()), item.isAvailable());
            cartItemResponseList.add(cartItemResponse);
        }
        return new CartResponse(cart.getId(), cartRepository.getBillAmount(cart.getId()).orElse(new BigDecimal(0)), cartItemResponseList);
    }

    private CartItem getCartItem(long cartItemId) throws CartItemNotFoundException {
        return cartItemRepository.findById(cartItemId).orElseThrow(CartItemNotFoundException::new);
    }
}
