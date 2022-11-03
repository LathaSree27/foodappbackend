package com.tweats.service;

import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;

    public void save(String name, BigDecimal price, Image image, Long category_id) {
        Item item = new Item(name, image, price, categoryRepository.findById(category_id).get());
        itemRepository.save(item);
    }

    public List<Item> get(Long category_id) {
        return itemRepository.findByCategory_id(category_id);
    }
}
