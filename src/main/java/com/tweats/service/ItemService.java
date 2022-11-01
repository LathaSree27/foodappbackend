package com.tweats.service;

import com.tweats.model.Item;
import com.tweats.repo.CategoryRepository;
import com.tweats.model.Image;
import com.tweats.repo.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ItemService {


    private ItemRepository itemRepository;


    private CategoryRepository categoryRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    public void save(String name, BigDecimal price, Image image, Long category_id)  {
        Item item = new Item(name, image, price, categoryRepository.findById(category_id).get());
        itemRepository.save(item);
    }


}
