package com.tweats.service;

import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
import com.tweats.exceptions.ItemDoesNotExistException;
import com.tweats.model.Cart;
import com.tweats.model.CartItem;
import com.tweats.model.Item;
import com.tweats.model.User;
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

    @Value("${application.link}")
    private String appLink;

    public CartService(UserPrincipalService userPrincipalService, ItemRepository itemRepository, CartRepository cartRepository) {
        this.userPrincipalService = userPrincipalService;
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    public void addItem(String userEmail, long itemId, long quantity) throws ItemDoesNotExistException {
        User user = userPrincipalService.findUserByEmail(userEmail);
        long userId = user.getId();
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) throw new ItemDoesNotExistException();
        Item item = optionalItem.get();
        long categoryId = item.getCategory().getId();
        Cart cart = cartRepository.findCartByUserIdAndCategoryId(userId, categoryId);
        CartItem cartItem = cart.getCartItem(item, quantity);
        cart.addCartItems(cartItem);
        cartRepository.save(cart);
    }

    public CartResponse cartItems(String email, long categoryId) {
        User user = userPrincipalService.findUserByEmail(email);
        Cart cart = cartRepository.findCartByUserIdAndCategoryId(user.getId(), categoryId);
        Set<CartItem> cartItems = cart.getCartItems();
        List<CartItemResponse> cartItemResponseList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), cartItem.getItem().getName(), cartItem.getQuantity(), cartItem.getItem().getPrice(), getImageLink(cartItem),cartItem.getItem().is_available());
            cartItemResponseList.add(cartItemResponse);
        }
        CartResponse cartResponse = new CartResponse(cart.getId(), cartRepository.getBillAmount(cart.getId()), cartItemResponseList);
        return cartResponse;
    }

    private String getImageLink(CartItem cartItem) {
        return appLink + "/images/" + (cartItem.getItem().getId());
    }

}