package com.tweats.service;

import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.repo.RoleRepository;
import com.tweats.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserPrincipleServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserPrincipalService userPrincipalService;
    private User user;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        user = mock(User.class);
        userPrincipalService = new UserPrincipalService(userRepository, roleRepository);
    }

    @Test
    void shouldBeAbleToReturnUserWhenAnAccountExistsWithGivenEmail() throws UserNotFoundException {
        String email = "abc@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThat(user, is(userPrincipalService.findUserByEmail(email)));
    }

    @Test
    void shouldBeAbleToThrowUserNotFoundExceptionWhenNoAccountExistsWithGivenMailId() {
        String email = "abc@gmail.com";

        assertThrows(UserNotFoundException.class, () -> userPrincipalService.findUserByEmail(email));
    }

    @Test
    void shouldBeAbleToReturnTrueWhenTheGivenUserIsVendor() {
        Role role = new Role("VENDOR");
        when(user.getRole()).thenReturn(role);
        when(roleRepository.findByName("VENDOR")).thenReturn(role);

        assertTrue(userPrincipalService.isVendor(user));
    }

    @Test
    void shouldBeAbleToReturnFalseWhenTheGivenUserIsNotVendor() {
        Role userRole = new Role("USER");
        Role vendorRole = new Role("VENDOR");
        when(user.getRole()).thenReturn(userRole);
        when(roleRepository.findByName("VENDOR")).thenReturn(vendorRole);

        assertFalse(userPrincipalService.isVendor(user));
    }
}
