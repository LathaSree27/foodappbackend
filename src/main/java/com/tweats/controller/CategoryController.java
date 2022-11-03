package com.tweats.controller;

import com.tweats.exceptions.NotAnImageException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.service.CategoryService;
import com.tweats.service.ImageService;
import com.tweats.service.UserPrincipalService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private ImageService imageService;
    private CategoryService categoryService;
    private UserPrincipalService userPrincipalService;


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestParam(value = "file") MultipartFile image,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "user_email") String user_email) throws IOException, NotAnImageException {
        Image categoryImage = imageService.save(image);

        categoryService.save(name, categoryImage, user_email);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public Map<String, Object> fetchCategory(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        String userEmail = principal.getName();
        User user = userPrincipalService.findUserByEmail(userEmail);
        Category category = categoryService.getCategory(user.getId());
        response.put("id", category.getId());
        return response;

    }

}
