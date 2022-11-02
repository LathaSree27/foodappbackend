package com.tweats.controller;

import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.UserRepository;
import com.tweats.service.CategoryService;
import com.tweats.service.ImageService;
import com.tweats.service.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/category")
public class CategoryController {


    private ImageService imageService;
    private CategoryService categoryService;

    @Autowired
    private UserPrincipalService userPrincipalService;

    public CategoryController(ImageService imageService, CategoryService categoryService) {
        this.imageService = imageService;
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value="file") MultipartFile image,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value="is_open") Boolean is_open,
                     @RequestParam(value="user_email") String user_email) throws IOException, NotAnImageException {
        Image categoryImage=imageService.save(image);

        categoryService.save(name, categoryImage, is_open, user_email);
    }
}
