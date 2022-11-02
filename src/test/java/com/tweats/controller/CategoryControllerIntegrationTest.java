package com.tweats.controller;

import com.tweats.TweatsApplication;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.RoleRepository;
import com.tweats.repo.UserRepository;
import com.tweats.service.ImageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@WithMockUser
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest {

    @Autowired
    ImageService imageService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    public void before() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @AfterEach
    public void after(){
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes());
        User vendor=userRepository.save(new User("abc","abc@example.com","password",roleRepository.save(new Role("VENDOR"))));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                .file(mockMultipartFile)
                .param("name","juice")
                .param("is_open", String.valueOf(true))
                .param("user_email",vendor.getEmail())
        )
                .andExpect(status().isCreated());
    }
}
