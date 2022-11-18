package com.tweats.controller;

import com.tweats.controller.response.VendorCategoryResponse;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.service.CategoryService;
import com.tweats.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "file") MultipartFile imageFile,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "user_email") String user_email) throws IOException, NotAnImageException {
        categoryService.save(name, imageFile, user_email);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public VendorCategoryResponse fetchCategory(Principal principal) throws NoCategoryFoundException {
        String userEmail = principal.getName();
        Category category = categoryService.getCategory(userEmail);
        VendorCategoryResponse vendorCategoryResponse = new VendorCategoryResponse(category.getId());
        return vendorCategoryResponse;

    }

}
