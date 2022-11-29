package com.tweats.view;

import com.tweats.TweatsApplication;
import com.tweats.model.*;
import com.tweats.repo.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
public class CartControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    private Category category;
    private Cart cart;
    private Image categoryImage;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role userRole = roleRepository.save(new Role("CUSTOMER"));
        User user = userRepository.save(new User("abc", "abc@gmail.com", bCryptPasswordEncoder.encode("password"), userRole));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        categoryImage = imageRepository.save(image);
        category = new Category("Juice", categoryImage, user);
        categoryRepository.save(category);
        cart = new Cart(category, user);
        cartRepository.save(cart);
    }

    @AfterEach
    public void after() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToAddItemToCartWhenCartApiIsInvoked() throws Exception {
        BigDecimal itemPrice = new BigDecimal(100);
        String itemName = "manchuria";
        Item item = new Item(itemName, categoryImage, itemPrice, category);
        Item savedItem = itemRepository.save(item);

        mockMvc.perform(post("/cart/item/" + savedItem.getId())
                .param("quantity", String.valueOf(1L))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isOk());
    }

    @Test
    void shouldThrowItemDoesNotExistErrorWhenItemWithGivenIdDoesNotExist() throws Exception {
        int itemId = -1;
        int quantity = 1;

        mockMvc.perform(post("/cart/item/" + itemId)
                .param("quantity", String.valueOf(quantity))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowNegativeQuantityErrorWhenItemQuantityIsGivenNegative() throws Exception {
        int itemId = 1;
        int quantity = -1;

        mockMvc.perform(post("/cart/item/" + itemId)
                .param("quantity", String.valueOf(quantity))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isBadRequest());
    }

    @Test
    void shouldBeAbleToGetCartItemsFromCartWhenCartApiIsInvoked() throws Exception {
        mockMvc.perform(get("/cart/category/" + category.getId())
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCategoryNotFoundErrorWhenCategoryDoestNotExistsWithGivenId() throws Exception {
        long categoryId = -1;

        mockMvc.perform(get("/cart/category/" + categoryId)
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfCartItemWhenCartUpdateApiIsInvoked() throws Exception {
        String itemName = "mango";
        long quantity = 1;
        Set<CartItem> cartItems = new HashSet<>();
        Item item = new Item(itemName, categoryImage, new BigDecimal(2), category);
        itemRepository.save(item);
        CartItem cartItem = new CartItem(cart, item, quantity);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        Cart cart = cartRepository.save(this.cart);
        List<CartItem> cartItemList = new ArrayList<>(cart.getCartItems());
        CartItem savedCartItem = cartItemList.get(0);
        int updatedQuantity = 2;

        mockMvc.perform(put("/cart/item/" + savedCartItem.getId())
                        .param("quantity", String.valueOf(updatedQuantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCartItemNotFoundErrorWhenCartItemDoesNotExistsWithGivenId() throws Exception {
        long cartItemId = -1;
        long quantity = 4;

        mockMvc.perform(put("/cart/item/" + cartItemId)
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowValidationErrorWhenGivenQuantityIsNegative() throws Exception {
        long cartItemId = 2;
        long quantity = -4;

        mockMvc.perform(put("/cart/item/" + cartItemId)
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenIdIsGiven() throws Exception {
        String itemName = "mango";
        long quantity = 1;
        Set<CartItem> cartItems = new HashSet<>();
        Item item = new Item(itemName, categoryImage, new BigDecimal(2), category);
        itemRepository.save(item);
        CartItem cartItem = new CartItem(cart, item, quantity);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        Cart cart = cartRepository.save(this.cart);
        List<CartItem> cartItemList = new ArrayList<>(cart.getCartItems());
        CartItem savedCartItem = cartItemList.get(0);

        mockMvc.perform(delete("/cart/item/" + savedCartItem.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCartItemNotFoundErrorWhenCartItemDoesNotExistWithGivenId() throws Exception {
        long cartItemId = 2;

        mockMvc.perform(delete("/cart/item/" + cartItemId))
                .andExpect(status().isNotFound());
    }
}
