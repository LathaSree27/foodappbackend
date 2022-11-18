package com.tweats.controller;

import com.tweats.TweatsApplication;
import com.tweats.controller.response.VendorCategoryResponse;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ImageRepository;
import com.tweats.repo.RoleRepository;
import com.tweats.repo.UserRepository;
import com.tweats.service.ImageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    private User vendor;

    @BeforeEach
    public void before() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        imageRepository.deleteAll();
        vendor = userRepository.save(new User("abc", "abc@example.com", bCryptPasswordEncoder.encode("password"), roleRepository.save(new Role("VENDOR"))));


    }

    @AfterEach
    public void after() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "juice")
                        .param("user_email", vendor.getEmail())
                        .with((httpBasic("abc@example.com", "password"))))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldBeAbleToFetchCategoryWhenUserEmailIsProvided() throws Exception {
        MockMultipartFile mockMultipartFileCategory = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image categoryImage = imageService.save(mockMultipartFileCategory);
        Category juice = categoryRepository.save(new Category("juice", categoryImage, vendor));
        VendorCategoryResponse vendorCategoryResponse = new VendorCategoryResponse(juice.getId());

        mockMvc.perform(get("/category")
                        .with((httpBasic("abc@example.com", "password"))))
                .andExpect(status().isOk())
                .andExpect(content().json(vendorCategoryResponse.toString()));

    }

    @Test
    void shouldThrowNoCategoryFoundErrorWhenVendorIsNotAssignedWithCategory() throws Exception {
        mockMvc.perform(get("/category")
                        .with((httpBasic("abc@example.com", "password"))))
                .andExpect(status().isNotFound());
    }
}
