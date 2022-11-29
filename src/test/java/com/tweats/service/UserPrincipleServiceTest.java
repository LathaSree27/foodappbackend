package com.tweats.service;

import com.tweats.controller.response.LoginResponse;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.repo.RoleRepository;
import com.tweats.repo.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPrincipleServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserPrincipalService userPrincipalService;
    @Mock
    private User user;

    @Test
    void shouldBeAbleToReturnUserWhenAnAccountExistsWithGivenEmail() throws UserNotFoundException {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThat(user, is(userPrincipalService.findUserByEmail(user.getEmail())));
    }

    @Test
    void shouldBeAbleToThrowUserNotFoundExceptionWhenNoAccountExistsWithGivenMailId() {
        assertThrows(UserNotFoundException.class, () -> userPrincipalService.findUserByEmail(user.getEmail()));
    }

    @Test
    void shouldBeAbleToReturnTrueWhenTheGivenUserIsVendor() {
        Role role = new Role("VENDOR");
        when(user.getRole()).thenReturn(role);
        when(roleRepository.findByName("VENDOR")).thenReturn(role);

        Assertions.assertTrue(userPrincipalService.isVendor(user));
    }

    @Test
    void shouldBeAbleToReturnFalseWhenTheGivenUserIsNotVendor() {
        Role userRole = new Role("USER");
        Role vendorRole = new Role("VENDOR");
        when(user.getRole()).thenReturn(userRole);
        when(roleRepository.findByName("VENDOR")).thenReturn(vendorRole);

        Assertions.assertFalse(userPrincipalService.isVendor(user));
    }

    @Test
    void shouldBeAbleToGetEmailAndRoleWhenUserIsLoggedIn() throws UserNotFoundException {
        String roleName = "CUSTOMER";
        String email = "abc@example.com";
        Role role = new Role(roleName);
        when(user.getRole()).thenReturn(role);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        LoginResponse expectedLoginResponse = new LoginResponse(email, user.getRole().getName());

        LoginResponse actualLoginResponse = userPrincipalService.getLoginResponse(email);

        assertThat(actualLoginResponse, is(expectedLoginResponse));
    }
}
