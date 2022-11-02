package com.tweats.service;

import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    public void save(String name, Image categoryImage, Boolean is_open, String email) {
        Category category = new Category(name, categoryImage, is_open, userRepository.findByEmail(email).get());
        categoryRepository.save(category);
    }

    public Optional<Category> getCategory(long id) {
        return categoryRepository.findById(id);
    }
}
