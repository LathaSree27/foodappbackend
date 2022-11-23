package com.tweats.service;

import com.tweats.model.Cart;
import com.tweats.model.CartItem;
import com.tweats.model.Item;
import com.tweats.model.User;
import com.tweats.repo.CartRepository;
import com.tweats.repo.ItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class CartService {
    UserPrincipalService userPrincipalService;
    ItemRepository itemRepository;

    CartRepository cartRepository;

    public void addItem(String userEmail, long itemId, long quantity) {
        User user = userPrincipalService.findUserByEmail(userEmail);
        long userId = user.getId();
        Item item = itemRepository.findById(itemId).get();
        long categoryId = item.getCategory().getId();
        Cart cart = cartRepository.findCartByUserIdAndCategoryId(userId, categoryId);
        CartItem cartItem = cart.getCartItem(item, quantity);
        cart.addCartItems(cartItem);
        cartRepository.save(cart);
    }
}
