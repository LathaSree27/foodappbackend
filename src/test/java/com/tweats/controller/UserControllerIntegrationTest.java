package com.tweats.controller;


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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void before() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @AfterEach
    public void after() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToLoginSuccessfully() throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role role = new Role("CUSTOMER");
        roleRepository.save(role);
        User user = new User("abc", "abc@gmail.com", bCryptPasswordEncoder.encode("Password@123"), roleRepository.findByName("CUSTOMER"));
        userRepository.save(user);
        mockMvc.perform(get("/login")
                        .with(httpBasic("abc@gmail.com", "Password@123")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrowErrorWhenUserCredentialsIsInvalid() throws Exception {
        mockMvc.perform(get("/login")
                .with((httpBasic("abc@gmail.com", "Password@123"))))
                .andExpect(status().isUnauthorized());
    }


}
