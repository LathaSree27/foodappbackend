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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private Category category;
    private Order order;
    private User user;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private User otherVendor;

    @BeforeEach
    public void before() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user = userRepository.save(new User("abc", "abc@example.com", bCryptPasswordEncoder.encode("password"), roleRepository.save(new Role("VENDOR"))));
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
        otherVendor = userRepository.save(new User("xyz", "xyz@example.com", bCryptPasswordEncoder.encode("password"), roleRepository.save(new Role("VENDOR"))));
        Category food = new Category("Food", categoryImage, true, otherVendor);
        categoryRepository.save(food);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        mockMvc.perform(
                        get("/order/completed")
                                .param("categoryId", String.valueOf(category.getId()))
                                .param("date", dateFormat.format(date)))
                .andExpect(status().isOk());
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
        mockMvc.perform(
                        get("/order/active")
                                .with(httpBasic(user.getEmail(), "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowNoOrdersFoundErrorWhenThereAreNoActiveOrdersFound() throws Exception {
        mockMvc.perform(
                        get("/order/active")
                                .with(httpBasic(user.getEmail(), "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToCompleteTheOrderWhenOrderIdIsGiven() throws Exception {
        order.setDelivered(false);
        Order placedOrder = orderRepository.save(order);
        mockMvc.perform(
                        put("/order/complete")
                                .with(httpBasic(user.getEmail(), "password"))
                                .param("orderId", String.valueOf(placedOrder.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowOrderNotFoundErrorWhenTheGivenOrderIsNotPlaced() throws Exception {
        long orderId = 1;
        mockMvc.perform(
                        put("/order/complete")
                                .with(httpBasic(user.getEmail(), "password"))
                                .param("orderId", String.valueOf(orderId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowOrderCategoryMismatchErrorWhenTheGivenOrderDoesNotBelongToVendorCategory() throws Exception {
        order.setDelivered(false);
        Order placedOrder = orderRepository.save(order);
        mockMvc.perform(
                        put("/order/complete")
                                .with(httpBasic(otherVendor.getEmail(), "password"))
                                .param("orderId", String.valueOf(placedOrder.getId())))
                .andExpect(status().isBadRequest());
    }
}
