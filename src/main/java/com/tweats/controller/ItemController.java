package com.tweats.controller;

import com.tweats.controller.response.ItemListResponse;
import com.tweats.exceptions.*;
import com.tweats.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;

@Validated
@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(Principal principal,
                     @RequestParam(value = "name") @NotBlank(message = "Item name can't be empty!") String name,
                     @RequestParam(value = "price") @Min(value = 0, message = "Price can't be negative!") BigDecimal price,
                     @RequestParam(value = "file") MultipartFile itemImageFile) throws IOException, NotAnImageException, ImageSizeExceededException, UserNotFoundException {
        String userEmail = principal.getName();
        itemService.save(name, price, itemImageFile, userEmail);
    }

    @GetMapping("{category_id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ItemListResponse get(@PathVariable Long category_id) throws NoItemsFoundException {
        ItemListResponse itemListResponse = itemService.getItems(category_id);
        return itemListResponse;
    }


    @PutMapping("{id}")
    public void updateAvailability(Principal principal, @PathVariable long id) throws ItemAccessException, ItemDoesNotExistException, UserNotFoundException {
        itemService.updateAvailability(principal.getName(), id);
    }
}
