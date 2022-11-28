package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private Set<CartItem> cartItems;

    public Cart(Category category, User user) {
        this.category = category;
        this.user = user;
        this.cartItems = new HashSet<>();
    }

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
    }

    public CartItem getCartItem(Item item, long quantity) {
        for (CartItem cartItem : cartItems) {
            if (isCartItem(item, cartItem)) {
                cartItem.updateQuantity(quantity);
                return cartItem;
            }
        }
        return new CartItem(this, item, quantity);
    }

    private boolean isCartItem(Item item, CartItem cartItem) {
        return cartItem.getItem().equals(item);
    }

    public void emptyCart() {
        cartItems = new HashSet<>();
    }


}
