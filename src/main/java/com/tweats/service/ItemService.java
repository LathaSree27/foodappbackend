package com.tweats.service;

import com.tweats.controller.response.CategoryItemsResponse;
import com.tweats.controller.response.ItemResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.repo.ItemRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Setter
@AllArgsConstructor
public class ItemService {

    private ItemRepository itemRepository;
    private ImageService imageService;
    private CategoryService categoryService;

    public void save(String name, BigDecimal price, MultipartFile itemImageFile, String userEmail) throws IOException, NotAnImageException, ImageSizeExceededException, UserNotFoundException, NoCategoryFoundException {
        Image itemImage = imageService.save(itemImageFile);
        Category category = categoryService.getCategory(userEmail);
        Item item = new Item(name, itemImage, price, category);
        itemRepository.save(item);
    }

    public CategoryItemsResponse getCategoryItems(Long categoryId) throws NoItemsFoundException {
        List<Item> items = itemRepository.findByCategoryId(categoryId);
        List<ItemResponse> itemResponses = getItemResponses(items);
        if (isEmpty(itemResponses)) throw new NoItemsFoundException();
        return new CategoryItemsResponse(categoryId, itemResponses);
    }

    public void updateAvailability(String vendorEmail, long itemId) throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException, NoCategoryFoundException {
        Category userCategory = categoryService.getCategory(vendorEmail);
        Item item = getItem(itemId);
        Category itemCategory = item.getCategory();
        if (!isValidCategory(userCategory, itemCategory)) throw new ItemAccessException();
        item.setAvailable(!item.isAvailable());
        itemRepository.save(item);
    }

    public Item getItem(Long itemId) throws ItemDoesNotExistException {
        return itemRepository.findById(itemId).orElseThrow(ItemDoesNotExistException::new);
    }

    private boolean isValidCategory(Category userCategory, Category itemCategory) {
        return userCategory.equals(itemCategory);
    }

    private boolean isEmpty(List<ItemResponse> itemResponses) {
        return itemResponses.size() == 0;
    }

    private List<ItemResponse> getItemResponses(List<Item> items) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            String itemImageLink = imageService.getImageLink(item.getImage());
            ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), itemImageLink, item.getPrice(), item.isAvailable());
            itemResponses.add(itemResponse);
        }
        return itemResponses;
    }
}
