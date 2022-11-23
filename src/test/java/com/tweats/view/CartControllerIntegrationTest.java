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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Category category;
    private Cart cart;
    private User user;
    private Image categoryImage;

    @BeforeEach
    void setUp() {
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
}
