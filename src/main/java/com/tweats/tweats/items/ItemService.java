package com.tweats.tweats.items;

import com.tweats.tweats.category.CategoryRepository;
import com.tweats.tweats.image.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

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
