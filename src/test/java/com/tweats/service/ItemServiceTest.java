package com.tweats.service;

import com.tweats.controller.response.ItemListResponse;
import com.tweats.controller.response.ItemResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;
    private ItemService itemService;
    private ImageService imageService;
    private Category category;
    private Principal principal;
    private UserPrincipalService userPrincipalService;
    private User user;
    private Image image;


    @BeforeEach
    public void setUp() {
        user = mock(User.class);
        itemRepository = mock(ItemRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        userPrincipalService = mock(UserPrincipalService.class);
        imageService = mock(ImageService.class);
        itemService = new ItemService(itemRepository, categoryRepository, imageService, userPrincipalService);
        category = mock(Category.class);
        principal = mock(Principal.class);
        image = mock(Image.class);
    }

    @Test
    void shouldBeAbleToAddItemWhenItemIsGiven() throws IOException, NotAnImageException, ImageSizeExceededException, UserNotFoundException {
        String userEmail = "abc@gmail.com";
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        MockMultipartFile itemImageFile = new MockMultipartFile("itemImage.png", "ItemImage".getBytes(StandardCharsets.UTF_8));
        Item item = new Item(itemName, image, price, category);
        when(principal.getName()).thenReturn(userEmail);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        when(imageService.save(any())).thenReturn(image);

        itemService.save(itemName, price, itemImageFile, principal.getName());

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
        for (Item item : items) {
            String itemImageLink = "http://localhost:8080/tweats/api/v1/images/" + item.getImage().getId();
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

        assertThrows(NoItemsFoundException.class, () -> itemService.getItems(categoryId));
    }

    @Test
    void shouldBeAbleUpdateAvailabilityOfAnItemFromFalseToTrue() throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException {
        String vendorEmail = "abc@gmail.com";
        long itemId = 1;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item = new Item(itemName, image, price, category);
        item.setId(itemId);
        when(userPrincipalService.findUserByEmail(vendorEmail)).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.updateAvailability(vendorEmail, itemId);

        verify(itemRepository).save(item);
        assertThat(itemRepository.findById(itemId).get().is_available(), is(true));
    }

    @Test
    void shouldThrowItemAccessExceptionWhenVendorTriesToUpdateAvailabilityOfItemFromOtherCategory() throws UserNotFoundException {
        String vendorEmail = "abc@gmail.com";
        long itemId = 1;
        String itemName = "Mango";
        BigDecimal price = new BigDecimal(80);
        Item item = new Item(itemName, image, price, category);
        Category userCategory = new Category();
        when(userPrincipalService.findUserByEmail(vendorEmail)).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(userCategory);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemAccessException.class, () -> itemService.updateAvailability(vendorEmail, itemId));
    }

    @Test
    void shouldThrowItemDoesNotExistExceptionWhenItemDoesNotExistWithId() throws UserNotFoundException {
        String vendorEmail = "abc@gmail.com";
        long itemId = 1;
        Category userCategory = new Category();
        when(userPrincipalService.findUserByEmail(vendorEmail)).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(userCategory);

        assertThrows(ItemDoesNotExistException.class, () -> itemService.updateAvailability(vendorEmail, itemId));
    }
}
