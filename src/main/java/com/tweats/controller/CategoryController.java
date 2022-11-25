package com.tweats.controller;

import com.tweats.controller.response.CategoryResponse;
import com.tweats.controller.response.VendorCategoryResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "file") MultipartFile imageFile,
                     @RequestParam(value = "name") @NotBlank(message = "category name cannot be empty!") String name,
                     @RequestParam(value = "user_email") @Email @NotBlank(message = "email cannot be blank!") String user_email) throws IOException, NotAnImageException, UserNotFoundException, NotAVendorException, CategoryAlreadyAssignedException {
        categoryService.save(name, imageFile, user_email);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public VendorCategoryResponse fetchCategory(Principal principal) throws NoCategoryFoundException, UserNotFoundException {
        String userEmail = principal.getName();
        Category category = categoryService.getCategory(userEmail);
        return new VendorCategoryResponse(category.getId());
    }

    public List<CategoryResponse> fetchAllCategories() {
        return categoryService.getAllCategories();
    }
}
