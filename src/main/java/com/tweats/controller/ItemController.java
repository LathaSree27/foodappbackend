package com.tweats.controller;

import com.tweats.controller.response.ItemListResponse;
import com.tweats.exceptions.NoItemsFoundException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "name") String name,
                     @RequestParam(value = "price") BigDecimal price,
                     @RequestParam(value = "category_id") Long category_id,
                     @RequestParam(value = "file") MultipartFile itemImageFile) throws IOException, NotAnImageException {
        itemService.save(name, price, itemImageFile, category_id);
    }
    @GetMapping("{category_id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ItemListResponse get(@PathVariable Long category_id) throws NoItemsFoundException, MalformedURLException {
        ItemListResponse itemListResponse = itemService.getItems(category_id);
        return itemListResponse;
    }
}
