package com.tweats.service;

import com.tweats.controller.response.CategoryItemsResponse;
import com.tweats.controller.response.ItemResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private ItemService itemService;
    @Mock
    private ImageService imageService;
    @Mock
    private Category category;
    @Mock
    private Image image;

    @Test
    void shouldBeAbleToAddItemWhenItemIsGiven() throws NotAnImageException, ImageSizeExceededException, UserNotFoundException, NoCategoryFoundException, IOException {
        String userEmail = "abc@gmail.com";
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        MockMultipartFile itemImageFile = new MockMultipartFile("itemImage.png", "ItemImage".getBytes(StandardCharsets.UTF_8));
        Item item = new Item(itemName, image, price, category);
        when(categoryService.getCategory(userEmail)).thenReturn(category);
        when(imageService.save(any())).thenReturn(image);

        itemService.save(itemName, price, itemImageFile, userEmail);

        verify(itemRepository).save(item);
    }

    @Test
    void shouldBeAbleToFetchAllTheItemsWhenAValidCategoryIdIsGiven() throws NoItemsFoundException {
        List<Item> items = new ArrayList<>();
        Image itemImage = new Image();
        Long categoryId = 2L;
        String firstItemName = "Mango";
        BigDecimal firstItemPrice = new BigDecimal(80);
        Item mango = new Item(firstItemName, itemImage, firstItemPrice, category);
        items.add(mango);
        String secondItemName = "Apple";
        BigDecimal secondItemPrice = new BigDecimal(50);
        Item apple = new Item(secondItemName, itemImage, secondItemPrice, category);
        items.add(apple);
        List<ItemResponse> itemResponses = new ArrayList<>();
        String itemImageLink = "http://localhost:8080/tweats/api/v1/images/" + itemImage.getId();
        for (Item item : items) {
            ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), itemImageLink, item.getPrice(), item.isAvailable());
            itemResponses.add(itemResponse);
        }
        CategoryItemsResponse expectedCategoryItemsResponse = new CategoryItemsResponse(categoryId, itemResponses);
        when(imageService.getImageLink(itemImage)).thenReturn(itemImageLink);
        when(itemRepository.findByCategory_id(categoryId)).thenReturn(items);

        CategoryItemsResponse actualCategoryItemsResponse = itemService.getCategoryItems(categoryId);

        assertThat(actualCategoryItemsResponse, is(expectedCategoryItemsResponse));

    }

    @Test
    void shouldThrowNoItemsFoundExceptionWhenNoItemsPresentInGivenCategory() {
        long categoryId = 1;

        assertThrows(NoItemsFoundException.class, () -> itemService.getCategoryItems(categoryId));
    }

    @Test
    void shouldBeAbleUpdateAvailabilityOfAnItemFromTrueToFalse() throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException, NoCategoryFoundException {
        String vendorEmail = "abc@gmail.com";
        long itemId = 1;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item = new Item(itemName, image, price, category);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(categoryService.getCategory(vendorEmail)).thenReturn(category);

        itemService.updateAvailability(vendorEmail, itemId);

        verify(itemRepository).save(item);
        assertThat(itemRepository.findById(itemId).get().isAvailable(), is(false));
    }

    @Test
    void shouldThrowItemAccessExceptionWhenVendorTriesToUpdateAvailabilityOfItemFromOtherCategory() throws UserNotFoundException, NoCategoryFoundException {
        String vendorEmail = "abc@gmail.com";
        long itemId = 1;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Category vendorCategory = new Category();
        Item item = new Item(itemName, image, price, category);
        when(categoryService.getCategory(vendorEmail)).thenReturn(vendorCategory);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemAccessException.class, () -> itemService.updateAvailability(vendorEmail, itemId));
    }

    @Test
    void shouldThrowItemDoesNotExistExceptionWhenItemDoesNotExistWithId() {
        String vendorEmail = "abc@gmail.com";
        long itemId = 1;

        assertThrows(ItemDoesNotExistException.class, () -> itemService.updateAvailability(vendorEmail, itemId));
    }
}
