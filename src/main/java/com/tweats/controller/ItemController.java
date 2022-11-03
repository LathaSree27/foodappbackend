package com.tweats.controller;

import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.service.ImageService;
import com.tweats.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private ImageService imageService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "name") String name,
                     @RequestParam(value = "price") BigDecimal price,
                     @RequestParam(value = "category_id") Long category_id,
                     @RequestParam(value = "file") MultipartFile image) throws IOException, NotAnImageException {
        Image itemImage = imageService.save(image);
        itemService.save(name, price, itemImage, category_id);
    }
    @GetMapping("{category_id}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Item> get(@PathVariable Long category_id){
        List<Item> items = itemService.get(category_id);
        return items;

    }
}
