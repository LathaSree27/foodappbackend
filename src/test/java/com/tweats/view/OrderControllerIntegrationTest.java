package com.tweats.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweats.TweatsApplication;
import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.model.*;
import com.tweats.repo.*;
import com.tweats.service.OrderService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    OrderService orderService;

    @Autowired
    ObjectMapper objectMapper;

    private Category category;
    private Order order;

    @BeforeEach
    public void before() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userRepository.save(new User("abc", "abc@example.com", encoder.encode("password"), roleRepository.save(new Role("CUSTOMER"))));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        Image categoryImage = imageRepository.save(image);
        Category juice = new Category("Juice", categoryImage, true, user);
        category = categoryRepository.save(juice);
        BigDecimal itemPrice = new BigDecimal(30);
        Item mango = itemRepository.save(new Item("Mango", image, itemPrice, category));
        Item apple = itemRepository.save(new Item("Apple", image, itemPrice, category));
        Date date = new Date();
        order = new Order(date, user, category);
        Set<OrderedItem> orderedItems = new HashSet<>();
        orderedItems.add(new OrderedItem(order, mango, 2));
        orderedItems.add(new OrderedItem(order, apple, 4));
        order.setOrderedItems(orderedItems);
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
        order.setDelivered(true);
        orderRepository.save(order);
        Date date = order.getDate();
        CompletedOrdersResponse actualCompletedOrderResponse = orderService.getCompletedOrders(category.getId(), date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        mockMvc.perform(
                        get("/order/completed")
                                .param("categoryId", String.valueOf(category.getId()))
                                .param("date", dateFormat.format(date)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(actualCompletedOrderResponse)));
    }

    @Test
    void shouldThrowNoOrdersFoundErrorWhenThereIsNoCompletedOrdersAreFound() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        mockMvc.perform(
                        get("/order/completed")
                                .param("categoryId", String.valueOf(category.getId()))
                                .param("date", dateFormat.format(order.getDate())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToGetActiveOrdersWhenCategoryIdIsGiven() throws Exception {
        order.setDelivered(false);
        orderRepository.save(order);
        ActiveOrderResponse actualActiveOrderResponse = orderService.getActiveOrders(category.getId());
        mockMvc.perform(
                        get("/order/active")
                                .param("categoryId", String.valueOf(category.getId())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(actualActiveOrderResponse)));
    }

    @Test
    void shouldThrowNoOrdersFoundErrorWhenThereAreNoActiveOrdersFound() throws Exception {
        mockMvc.perform(
                get("/order/active")
                        .param("categoryId",String.valueOf(category.getId())))
                .andExpect(status().isNotFound());
    }
}
