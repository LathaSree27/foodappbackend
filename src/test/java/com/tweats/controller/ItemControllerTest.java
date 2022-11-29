package com.tweats.controller;

import com.tweats.controller.response.CategoryItemsResponse;
import com.tweats.exceptions.*;
import com.tweats.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    @Mock
    private Principal principal;

    @Test
    void shouldBeAbleToSaveItemWhenNamePriceAndFileIsGiven() throws UserNotFoundException, NoCategoryFoundException, ImageSizeExceededException, IOException, NotAnImageException {
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        MockMultipartFile itemImageFile = new MockMultipartFile("itemImage.png", "ItemImage".getBytes(StandardCharsets.UTF_8));

        itemController.save(principal, itemName, price, itemImageFile);

        verify(itemService).save(itemName, price, itemImageFile, principal.getName());
    }

    @Test
    void shouldBeAbleToGetAllItemsInTheCategoryWhenCategoryIdIsGiven() throws NoItemsFoundException {
        long categoryId = 1;
        CategoryItemsResponse expectedCategoryItemsResponse = new CategoryItemsResponse();
        when(itemService.getCategoryItems(categoryId)).thenReturn(expectedCategoryItemsResponse);

        CategoryItemsResponse actualCategoryItemsResponse = itemController.getCategoryItems(categoryId);

        verify(itemService).getCategoryItems(categoryId);
        assertThat(actualCategoryItemsResponse, is(expectedCategoryItemsResponse));
    }

    @Test
    void shouldBeAbleToUpdateAvailabilityOfItem() throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException, NoCategoryFoundException {
        long itemId = 1;

        itemController.updateAvailability(principal, itemId);

        verify(itemService).updateAvailability(principal.getName(), itemId);
    }
}
