package com.tweats.tweats.items;

import com.tweats.tweats.category.Category;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ItemRequest {
    private final String name;
    private final BigDecimal price;
    private final Category category;
    private MultipartFile image;

    public ItemRequest(String name, BigDecimal price, Category category, MultipartFile image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }
}
