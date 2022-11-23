package com.tweats.view;

import com.tweats.TweatsApplication;
import com.tweats.model.*;
import com.tweats.repo.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
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
    private Category savedCategory;
    private Image savedImage;
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
        Role userRole = new Role("USER");
        Role savedRole = roleRepository.save(userRole);
        user = userRepository.save(new User("abc", "abc@gmail.com", bCryptPasswordEncoder.encode("password"), savedRole));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        categoryImage = imageRepository.save(image);
        savedImage = imageRepository.save(categoryImage);
        category = new Category("Juice", categoryImage, false, user);
        savedCategory = categoryRepository.save(category);
        cart = new Cart(savedCategory, user);
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
    void shouldBeAbleToInvokeCartApi() throws Exception {
        BigDecimal itemPrice = new BigDecimal(100);
        String itemName = "manchuria";
        Item item = new Item(itemName, savedImage, itemPrice,savedCategory);
        Item savedItem = itemRepository.save(item);

        mockMvc.perform(post("/cart")
                .param("itemId",String.valueOf(savedItem.getId()))
                .param("quantity", String.valueOf(1L))
                .with(httpBasic("abc@gmail.com", "password"))).andExpect(status().isOk());
    }
}
