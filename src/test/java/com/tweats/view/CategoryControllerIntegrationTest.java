package com.tweats.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweats.TweatsApplication;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    ObjectMapper objectMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private User vendor;
    private User admin;

    @BeforeEach
    public void before() {
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        vendor = userRepository.save(new User("abc", "abc@example.com", bCryptPasswordEncoder.encode("password"), roleRepository.save(new Role("VENDOR"))));
        admin = userRepository.save(new User("xyz", "xyz@example.com", bCryptPasswordEncoder.encode("password"), roleRepository.save(new Role("ADMIN"))));
    }

    @AfterEach
    public void after() {
        categoryRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "juice")
                        .param("user_email", vendor.getEmail())
                        .with((httpBasic(admin.getEmail(), "password"))))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldBeAbleToFetchCategoryWhenUserEmailIsProvided() throws Exception {
        MockMultipartFile mockMultipartFileCategory = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image categoryImage = imageService.save(mockMultipartFileCategory);
        categoryRepository.save(new Category("juice", categoryImage, vendor));

        mockMvc.perform(get("/category/vendor")
                        .with((httpBasic(vendor.getEmail(), "password"))))
                .andExpect(status().isOk());

    }

    @Test
    void shouldThrowNoCategoryFoundErrorWhenVendorIsNotAssignedWithCategory() throws Exception {
        mockMvc.perform(get("/category/vendor")
                        .with((httpBasic(vendor.getEmail(), "password"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowNotAVendorErrorWhenTheGivenUserIsNotAVendor() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "juice")
                        .param("user_email", admin.getEmail())
                        .with((httpBasic(admin.getEmail(), "password"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldThrowUserNotFoundErrorWhenNoAccountExistsWithTheGivenEmailId() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "juice")
                        .param("user_email", "pqr@gmail.com")
                        .with((httpBasic(admin.getEmail(), "password"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowCategoryAlreadyAssignedErrorWhenTheGivenVendorHasCategoryAssigned() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image image = new Image("image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes(), 22L);

        Image categoryImage = imageRepository.save(image);
        Category juice = new Category("juice", categoryImage, vendor);
        categoryRepository.save(juice);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "juice")
                        .param("user_email", vendor.getEmail())
                        .with((httpBasic(admin.getEmail(), "password"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldNotBeAbleToSaveCategoryWhenInvalidCategoryDetailsAreProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "")
                        .param("user_email", "")
                        .with((httpBasic(admin.getEmail(), "password"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotBeAbleToSaveCategoryWhenInvalidEmailIdIsProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(mockMultipartFile)
                        .param("name", "juice")
                        .param("user_email", "abc")
                        .with((httpBasic(admin.getEmail(), "password"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void shouldBeAbleToGetAllCategories() throws Exception {
        MockMultipartFile mockMultipartFileCategory = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image categoryImage = imageService.save(mockMultipartFileCategory);
        categoryRepository.save(new Category("juice", categoryImage, vendor));

        mockMvc.perform(
                get("/category")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void shouldThrowNoCategoryFoundErrorWhenNoCategoriesAreAvailable() throws Exception {
        mockMvc.perform(
                get("/category")
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToUpdateOpenStatusForVendorCategory() throws Exception {
        MockMultipartFile mockMultipartFileCategory = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        Image categoryImage = imageService.save(mockMultipartFileCategory);
        categoryRepository.save(new Category("juice", categoryImage, vendor));

        mockMvc.perform(
                put("/category/status")
                        .with(httpBasic(vendor.getEmail(), "password"))
        ).andExpect(status().isOk());
    }
}
