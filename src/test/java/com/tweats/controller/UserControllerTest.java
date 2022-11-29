package com.tweats.controller;

import com.tweats.controller.response.LoginResponse;
import com.tweats.service.UserPrincipalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserPrincipalService userPrincipalService;
    @Mock
    private Principal principal;

    @Test
    void shouldBeAbleToGetEmailAndRoleWhenUserLoggedIn() {
        LoginResponse expectedLoginResponse = new LoginResponse();
        when(userPrincipalService.getLoginResponse(principal.getName())).thenReturn(expectedLoginResponse);

        LoginResponse actualLoginResponse = userController.login(principal);

        verify(userPrincipalService).getLoginResponse(principal.getName());
        assertThat(actualLoginResponse, is(expectedLoginResponse));
    }
}
