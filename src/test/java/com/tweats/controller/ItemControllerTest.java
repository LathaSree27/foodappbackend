package com.tweats.controller;

import com.tweats.exceptions.*;
import com.tweats.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static org.mockito.Mockito.*;

public class ItemControllerTest {

    private ItemService itemService;
    private ItemController itemController;
    private Principal principal;

    @BeforeEach
    void setUp() {
        itemService = mock(ItemService.class);
        itemController = new ItemController(itemService);
        principal = mock(Principal.class);
    }

    @Test
    void shouldBeAbleToUpdateAvailabilityOfItem() throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException {
        long itemId = 1;
        String vendorEmail = "abc@gmail.com";
        when(principal.getName()).thenReturn(vendorEmail);

        itemController.updateAvailability(principal, itemId);

        verify(itemService).updateAvailability(vendorEmail, itemId);
    }
}
