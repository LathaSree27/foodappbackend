package com.tweats.tweats.users.view;

import com.tweats.tweats.TweatsApplication;
import com.tweats.tweats.users.Role;
import com.tweats.tweats.users.RoleRepository;
import com.tweats.tweats.users.User;
import com.tweats.tweats.users.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TweatsApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
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
