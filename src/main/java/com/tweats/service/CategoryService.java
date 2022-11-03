package com.tweats.service;

import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    public void save(String name, Image categoryImage, String email) {
        Category category = new Category(name, categoryImage, userRepository.findByEmail(email).get());
        categoryRepository.save(category);
    }

    public Category getCategory(long userId) {
        return categoryRepository.findByUser_id(userId);
    }
}
