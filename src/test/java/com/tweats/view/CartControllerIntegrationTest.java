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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
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
    private Category category;
    private Cart cart;
    private Image categoryImage;
    private User user;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role userRole = roleRepository.save(new Role("CUSTOMER"));
        user = userRepository.save(new User("abc", "abc@gmail.com", bCryptPasswordEncoder.encode("password"), userRole));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        categoryImage = imageRepository.save(image);
        category = new Category("Juice", categoryImage, user);
        categoryRepository.save(category);
        cart = new Cart(category, user);
        cartRepository.save(cart);
    }

    @AfterEach
    public void after() {
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
        Item savedItem = itemRepository.save(item);
        CartItem cartItem = new CartItem(cart, item, quantity);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        Cart savedCart = cartRepository.save(cart);
        int updatedQuantity = 2;

        mockMvc.perform(put("/cart/" + savedCart.getId())
                        .param("itemId", String.valueOf(savedItem.getId()))
                        .param("quantity", String.valueOf(updatedQuantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCartItemNotFoundErrorWhenCartItemDoesNotExistsWithGivenItem() throws Exception {
        long itemId = 1;
        long quantity = 4;

        mockMvc.perform(put("/cart/" + cart.getId())
                        .param("itemId", String.valueOf(itemId))
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowValidationErrorWhenGivenQuantityIsNegative() throws Exception {
        long itemId = 2;
        long quantity = -4;

        mockMvc.perform(put("/cart/" + cart.getId())
                        .param("itemId", String.valueOf(itemId))
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowCartAccessDeniedErrorWhenGivenCartDoesNotBelongsToUser() throws Exception {
        long itemId = 2;
        long quantity = 4;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role userRole = roleRepository.save(new Role("CUSTOMER"));
        User user2 = userRepository.save(new User("ab", "ab@gmail.com", bCryptPasswordEncoder.encode("password"), userRole));

        mockMvc.perform(put("/cart/" + cart.getId())
                        .param("itemId", String.valueOf(itemId))
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic(user2.getEmail(), "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldThrowCartNotFoundExceptionWhenCartDoesNotFoundWithGivenId() throws Exception {
        long itemId = 2;
        long quantity = 4;
        int cartId = 1;

        mockMvc.perform(put("/cart/" + cartId)
                        .param("itemId", String.valueOf(itemId))
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic(user.getEmail(), "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToDeleteCartItemWhenCartIdAndItemIdAreGiven() throws Exception {
        String itemName = "mango";
        long quantity = 1;
        Set<CartItem> cartItems = new HashSet<>();
        Item item = new Item(itemName, categoryImage, new BigDecimal(2), category);
        itemRepository.save(item);
        CartItem cartItem = new CartItem(cart, item, quantity);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        Cart savedCart = cartRepository.save(this.cart);

        mockMvc.perform(delete("/cart/" + savedCart.getId())
                        .param("itemId", String.valueOf(item.getId()))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowCartNotFoundErrorWhenCartItemDoesNotExistWithGivenId() throws Exception {
        long itemId = 2;
        long cartId = 3;

        mockMvc.perform(delete("/cart/" + cartId)
                        .param("itemId", String.valueOf(itemId))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowCartItemNotFoundErrorWhenCartItemDoesNotExistsWithGivenItemId() throws Exception {
        long itemId = 1;
        long quantity = 4;

        mockMvc.perform(put("/cart/" + cart.getId())
                        .param("itemId", String.valueOf(itemId))
                        .param("quantity", String.valueOf(quantity))
                        .with(httpBasic("abc@gmail.com", "password")))
                .andExpect(status().isNotFound());
    }
}

