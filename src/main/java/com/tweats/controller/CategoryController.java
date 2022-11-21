package com.tweats.controller;

import com.tweats.controller.response.VendorCategoryResponse;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.NotAVendorException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Category;
import com.tweats.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "file") MultipartFile imageFile,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "user_email") String user_email) throws IOException, NotAnImageException, UserNotFoundException, NotAVendorException {
        categoryService.save(name, imageFile, user_email);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public VendorCategoryResponse fetchCategory(Principal principal) throws NoCategoryFoundException, UserNotFoundException {
        String userEmail = principal.getName();
        Category category = categoryService.getCategory(userEmail);
        VendorCategoryResponse vendorCategoryResponse = new VendorCategoryResponse(category.getId());
        return vendorCategoryResponse;

    }

}
