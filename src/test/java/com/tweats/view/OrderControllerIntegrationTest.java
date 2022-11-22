package com.tweats.view;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
public class OrderControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ObjectMapper objectMapper;

    private User user;
    private Image categoryImage;
    private Category category;
    private Item mango;
    private Item apple;


    @BeforeEach
    public void before() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user = userRepository.save(new User("abc", "abc@example.com", encoder.encode("password"), roleRepository.save(new Role("CUSTOMER"))));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        categoryImage = imageRepository.save(image);
        Category juice = new Category("Juice", categoryImage, true, user);
        category = categoryRepository.save(juice);
        BigDecimal itemPrice = new BigDecimal(30);
        mango = itemRepository.save(new Item("Mango", image, itemPrice, category));
        apple = itemRepository.save(new Item("Apple", image, itemPrice, category));

    }

    @AfterEach
    public void after() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToGetCompletedOrdersWhenCategoryIdAndDateIsGiven() throws Exception {
        Date date = new Date();
        Order order = new Order(date, user, category);
        Set<OrderedItem> orderedItems = new HashSet<>();
        orderedItems.add(new OrderedItem(order,mango,2));
        orderedItems.add(new OrderedItem(order,apple,4));
        order.setOrderedItems(orderedItems);
        order.setDelivered(true);
        orderRepository.save(order);

        mockMvc.perform(
                get("/orders/completed")
                        .param("category_id",String.valueOf(category.getId()))
                        .param("date","2022-11-22"))
                .andExpect(status().isOk());


    }
}
