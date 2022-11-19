package com.tweats.service;

import com.tweats.controller.response.ItemListResponse;
import com.tweats.controller.response.ItemResponse;
import com.tweats.exceptions.NoItemsFoundException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;
    private ItemService itemService;
    private ImageService imageService;
    private Category category;

    @BeforeEach
    public void setUp() {
        itemRepository = mock(ItemRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        imageService = mock(ImageService.class);
        itemService = new ItemService(itemRepository, categoryRepository, imageService);
        category = mock(Category.class);
    }

    @Test
    void shouldBeAbleToAddItemWhenItemIsGiven() throws IOException, NotAnImageException {
        long categoryId = 1;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        MockMultipartFile itemImageFile = new MockMultipartFile("itemImage.png", "ItemImage".getBytes(StandardCharsets.UTF_8));
        Item item = new Item();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        itemService.save(itemName, price, itemImageFile, categoryId);

        verify(itemRepository).save(item);
    }

    @Test
    void shouldBeAbleToFetchAllTheItemsWhenAValidCategoryIdIsGiven() throws NoItemsFoundException, MalformedURLException {
        List<Item> items = new ArrayList<>();
        Image itemImage = new Image();
        Long category_id = 2L;
        String firstItemName = "Mango";
        BigDecimal firstItemPrice = new BigDecimal(80);
        Item mango = new Item(firstItemName, itemImage, firstItemPrice, category);
        items.add(mango);
        String secondItemName = "Apple";
        BigDecimal secondItemPrice = new BigDecimal(50);
        Item apple = new Item(secondItemName, itemImage, secondItemPrice, category);
        items.add(apple);
        String appLink = "http://localhost:8080/tweats/api/v1";
        itemService.setAppLink(appLink);
        List<ItemResponse> itemResponses = new ArrayList<>();
        for(Item item: items){
            String itemImageLink = "http://localhost:8080/tweats/api/v1/images/" +item.getImage().getId();
            ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), itemImageLink, item.getPrice(), item.is_available());
            itemResponses.add(itemResponse);
        }
        ItemListResponse expectedItemListResponse = new ItemListResponse(category_id, itemResponses);
        when(itemRepository.findByCategory_id(category_id)).thenReturn(items);

        ItemListResponse actualItemListResponse = itemService.getItems(category_id);

        assertThat(actualItemListResponse, is(expectedItemListResponse));

    }

    @Test
    void shouldThrowNoItemsFoundExceptionWhenNoItemsPresentInGivenCategory() {
        long categoryId = 1;

        assertThrows(NoItemsFoundException.class, ()-> itemService.getItems(categoryId));
    }
}
