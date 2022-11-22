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
import java.security.Principal;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(Principal principal,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "price") BigDecimal price,
                     @RequestParam(value = "file") MultipartFile itemImageFile) throws IOException, NotAnImageException {
        String userEmail = principal.getName();
        itemService.save(name, price, itemImageFile, userEmail);
    }

    @GetMapping("{category_id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ItemListResponse get(@PathVariable Long category_id) throws NoItemsFoundException {
        ItemListResponse itemListResponse = itemService.getItems(category_id);
        return itemListResponse;
    }
}
