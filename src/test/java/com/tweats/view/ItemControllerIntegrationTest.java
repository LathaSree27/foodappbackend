package com.tweats.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweats.TweatsApplication;
import com.tweats.controller.response.ItemListResponse;
import com.tweats.controller.response.ItemResponse;
import com.tweats.model.*;
import com.tweats.repo.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser
public class ItemControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ImageRepository imageRepository;


    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ObjectMapper objectMapper;
    private User vendor;
    private Image categoryImage;
    private Category category;
    private BCryptPasswordEncoder encoder;


    @BeforeEach
    public void before() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        encoder = new BCryptPasswordEncoder();
        vendor = userRepository.save(new User("abc", "abc@example.com", encoder.encode("password"), roleRepository.save(new Role("VENDOR"))));
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 1L);
        categoryImage = imageRepository.save(image);
        Category juice = new Category("Juice", categoryImage, true, vendor);
        category = categoryRepository.save(juice);
    }

    @AfterEach
    public void after() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToSaveItemWhenTheItemIsGiven() throws Exception {
        MockMultipartFile ItemImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/item")
                        .file(ItemImage)
                        .param("name", "Mango")
                        .param("price", String.valueOf(new BigDecimal(80)))
                        .with(httpBasic("abc@example.com", "password")))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldThrowValidationErrorWhenPriceIsNegative() throws Exception {
        MockMultipartFile ItemImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/item")
                        .file(ItemImage)
                        .param("name", "mango")
                        .param("price", String.valueOf(new BigDecimal(-1)))
                        .with(httpBasic("abc@example.com", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationErrorWhenNameIsBlank() throws Exception {
        MockMultipartFile ItemImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/item")
                        .file(ItemImage)
                        .param("name", "")
                        .param("price", String.valueOf(new BigDecimal(80)))
                        .with(httpBasic("abc@example.com", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBeAbleToFetchAllTheItemsWhenAValidCategoryIdIsGiven() throws Exception {
        BigDecimal itemPrice = new BigDecimal(100);
        String itemName = "mango";
        Item item = new Item(itemName, categoryImage, itemPrice, category);
        Item savedItem = itemRepository.save(item);
        List<ItemResponse> itemResponses = new ArrayList<>();
        String imageLink = "http://localhost:8080/tweats/api/v1/images/" + savedItem.getImage().getId();
        itemResponses.add(new ItemResponse(savedItem.getId(), savedItem.getName(), imageLink, savedItem.getPrice(), savedItem.is_available()));
        ItemListResponse itemListResponse = new ItemListResponse(category.getId(), itemResponses);


        mockMvc.perform(get("/item/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemListResponse)));
    }

    @Test
    void shouldThrowNoItemsFoundErrorWhenThereIsNoItemsInTheGivenCategoryId() throws Exception {
        mockMvc.perform(get("/item/" + category.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToUpdateAvailabilityOfAnItemWhenVendorBelongsToThatParticularCategoryOfItem() throws Exception {
        BigDecimal itemPrice = new BigDecimal(100);
        String itemName = "mango";
        Item item = new Item(itemName, categoryImage, itemPrice, category);
        Item savedItem = itemRepository.save(item);
        mockMvc.perform(put("/item/"+savedItem.getId())
                        .with(httpBasic("abc@example.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowItemAccessErrorWhenVendorDoesNotBelongToTheCategoryOfItem() throws Exception {
        User foodVendor = userRepository.save(new User("abc", "xyz@example.com", encoder.encode("password"), roleRepository.save(new Role("VENDOR"))));
        Category food = new Category("food", categoryImage, true, foodVendor);
        Category foodCategory = categoryRepository.save(food);
        BigDecimal itemPrice = new BigDecimal(100);
        String itemName = "manchuria";
        Item item = new Item(itemName, categoryImage, itemPrice, foodCategory);
        Item savedItem = itemRepository.save(item);

        mockMvc.perform(put("/item/" + savedItem.getId())
                        .with(httpBasic("abc@example.com", "password")))
                .andExpect(status().isForbidden());
    }
}
