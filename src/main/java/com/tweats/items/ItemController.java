package com.tweats.tweats.items;

import com.tweats.tweats.category.Category;
import com.tweats.tweats.exceptions.NotAnImageException;
import com.tweats.tweats.image.Image;
import com.tweats.tweats.image.ImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/v1")
public class ItemController {


    private ItemService itemService;
    private ImageService imageService;

    @Autowired
    public ItemController(ItemService itemService, ImageService imageService) {
        this.itemService = itemService;
        this.imageService = imageService;
    }

    @PostMapping("/item")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "name") String name, @RequestParam(value = "price") BigDecimal price,
                     @RequestParam(value = "category_id") Long category_id,
                     @RequestParam(value = "file") MultipartFile image) throws IOException, NotAnImageException {
        Image itemImage = imageService.save(image);
        itemService.save(name, price, itemImage, category_id);
    }

}
