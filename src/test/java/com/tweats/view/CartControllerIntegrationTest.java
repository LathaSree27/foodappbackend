package com.tweats.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweats.TweatsApplication;
import com.tweats.controller.response.CartItemResponse;
import com.tweats.controller.response.CartResponse;
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
    @Autowired
    ObjectMapper objectMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Category category;
    private Cart cart;
    private User user;
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
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role userRole = roleRepository.save(new Role("USER"));
        user = userRepository.save(new User("abc", "abc@gmail.com", bCryptPasswordEncoder.encode("password"), userRole));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        categoryImage = imageRepository.save(image);
        category = new Category("Juice", categoryImage, false, user);
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

        mockMvc.perform(post("/cart")
                .param("itemId", String.valueOf(savedItem.getId()))
                .param("quantity", String.valueOf(1L))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isOk());
    }

    @Test
    void shouldThrowItemDoesNotExistErrorWhenItemWithGivenIdDoesNotExist() throws Exception {
        mockMvc.perform(post("/cart")
                .param("itemId", "1")
                .param("quantity", String.valueOf(1L))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowNegativeQuantityErrorWhenItemQuantityIsGivenNegative() throws Exception {
        mockMvc.perform(post("/cart")
                .param("itemId", "1")
                .param("quantity", String.valueOf(-1L))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isBadRequest());
    }

    @Test
    void shouldBeAbleToGetCartItemsFromCartWhenCartApiIsInvoked() throws Exception {
        String appLink = "http://localhost:8080/tweats/api/v1";
        CartItemResponse mango = CartItemResponse.builder().id(1).name("Mango").quantity(2).price(new BigDecimal(50)).imageLink(appLink + "/images/" + 1).build();
        ArrayList<CartItemResponse> cartItemResponses = new ArrayList<>();
        cartItemResponses.add(mango);
        CartResponse cartResponse = CartResponse.builder().id(2).billAmount(new BigDecimal(100)).cartItemResponses(cartItemResponses).build();
        String jsonContent = objectMapper.writeValueAsString(cartResponse);

        mockMvc.perform(get("/cart/" + category.getId())
                        .with(httpBasic("abc@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCategoryNotFoundErrorWhenCategoryDoestNotExistsWithGivenId() throws Exception {
        mockMvc.perform(get("/cart/" + 370)
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToUpdateQuantityOfCartItemWhenCartUpdateApiIsInvoked() throws Exception {
        String itemName = "mango";
        Item item = new Item(itemName, categoryImage, new BigDecimal(2), category);
        itemRepository.save(item);
        long quantity = 1;
        CartItem cartItem = new CartItem(cart, item, quantity);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        Cart cart = cartRepository.save(this.cart);
        List<CartItem> cartItemList = new ArrayList<>(cart.getCartItems());
        CartItem savedCartItem = cartItemList.get(0);

        mockMvc.perform(put("/cart/update/" + savedCartItem.getId())
                        .param("quantity", String.valueOf(2))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCartItemNotFoundErrorWhenCartItemDoesNotExistsWithGivenId() throws Exception {
        long cartItemId = 2L;
        long quantity = 4L;

        mockMvc.perform(put("/cart/update/" + cartItemId)
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowValidationErrorWhenGivenQuantityIsNegative() throws Exception {
        long cartItemId = 2L;
        long quantity = -4L;

        mockMvc.perform(put("/cart/update/" + cartItemId)
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenIdIsGiven() throws Exception {
        String itemName = "mango";
        Item item = new Item(itemName, categoryImage, new BigDecimal(2), category);
        itemRepository.save(item);
        long quantity = 1;
        CartItem cartItem = new CartItem(cart, item, quantity);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        Cart cart = cartRepository.save(this.cart);
        List<CartItem> cartItemList = new ArrayList<>(cart.getCartItems());
        CartItem savedCartItem = cartItemList.get(0);

        mockMvc.perform(delete("/cart/delete/" + savedCartItem.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCartItemNotFoundErrorWhenCartItemDoesNotExistWithGivenId() throws Exception {
        long cartItemId = 2L;

        mockMvc.perform(delete("/cart/delete/" + cartItemId))
                .andExpect(status().isNotFound());
    }
}
