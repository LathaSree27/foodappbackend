package com.tweats.controller;

import com.tweats.TweatsApplication;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.repo.*;
import com.tweats.service.ImageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@WithMockUser
@ActiveProfiles("test")
@AutoConfigureMockMvc
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
    ImageService imageService;

    public ItemControllerIntegrationTest() {
        this.imageService = new ImageService(imageRepository);
    }

    @BeforeEach
    public void before() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
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
        User vendor = userRepository.save(new User("abc", "abc@example.com", "password", roleRepository.save(new Role("VENDOR"))));
        MockMultipartFile mockedCategoryImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image categoryImage = imageService.save(mockedCategoryImage);
        Category category = new Category("Juice", categoryImage, true, vendor);
        Category juice = categoryRepository.save(category);
        MockMultipartFile ItemImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/item")
                        .file(ItemImage)
                        .param("name", "Mango")
                        .param("price", String.valueOf(new BigDecimal(80)))
                        .param("category_id", String.valueOf(juice.getId()))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldBeAbleToFetchAllTheItemsWhenAValidCategoryIdIsGiven() throws Exception {
        User vendor = userRepository.save(new User("abc", "abc@example.com", "password", roleRepository.save(new Role("VENDOR"))));
        MockMultipartFile mockedCategoryImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image categoryImage = imageService.save(mockedCategoryImage);
        Category category = new Category("Juice", categoryImage, true, vendor);
        Category juice = categoryRepository.save(category);
        long categoryId = juice.getId();

        mockMvc.perform(get("/item/"+categoryId))
                .andExpect(status().isOk());
    }
}
