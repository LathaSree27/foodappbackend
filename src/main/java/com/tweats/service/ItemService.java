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
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Setter
public class ItemService {

    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;
    private ImageService imageService;
    private UserPrincipalService userPrincipalService;

    @Value("${application.link}")
    private String appLink;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, ImageService imageService, UserPrincipalService userPrincipalService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
        this.userPrincipalService = userPrincipalService;
    }

    public void save(String name, BigDecimal price, MultipartFile itemImageFile, String userEmail) throws IOException, NotAnImageException, ImageSizeExceededException, UserNotFoundException {
        Image itemImage = imageService.save(itemImageFile);
        User vendor = userPrincipalService.findUserByEmail(userEmail);
        Category category = categoryRepository.findByUserId(vendor.getId());
        Item item = new Item(name, itemImage, price, category);
        itemRepository.save(item);
    }

    public ItemListResponse getItems(Long category_id) throws NoItemsFoundException {
        List<Item> items = itemRepository.findByCategory_id(category_id);
        List<ItemResponse> itemResponses = getItemResponses(items);
        if (isEmpty(itemResponses)) throw new NoItemsFoundException();
        ItemListResponse itemListResponse = new ItemListResponse(category_id, itemResponses);
        return itemListResponse;
    }

    private boolean isEmpty(List<ItemResponse> itemResponses) {
        return itemResponses.size() == 0;
    }

    private List<ItemResponse> getItemResponses(List<Item> items) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            String itemImageLink = getItemImageLink(item.getImage().getId());
            ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), itemImageLink, item.getPrice(), item.is_available());
            itemResponses.add(itemResponse);
        }
        return itemResponses;
    }

    private String getItemImageLink(String itemImageId) {
        return appLink + "/images/" + itemImageId;
    }

    public void updateAvailability(String vendorEmail, long itemId) throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException {
        User vendor = userPrincipalService.findUserByEmail(vendorEmail);
        Category userCategory = categoryRepository.findByUserId(vendor.getId());
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Item item = getItem(optionalItem);
        Category itemCategory = item.getCategory();
        if (!isValidCategory(userCategory, itemCategory)) throw new ItemAccessException();
        item.set_available(item.is_available() ? false : true);
        itemRepository.save(item);
    }

    private Item getItem(Optional<Item> optionalItem) throws ItemDoesNotExistException {
        if (!optionalItem.isPresent()) throw new ItemDoesNotExistException();
        Item item = optionalItem.get();
        return item;
    }

    private boolean isValidCategory(Category userCategory, Category itemCategory) {
        return userCategory.equals(itemCategory);
    }
}
