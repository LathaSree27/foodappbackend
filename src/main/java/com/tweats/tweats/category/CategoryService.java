package com.tweats.tweats.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public void save(Category category) {
        categoryRepository.save(category);
    }

    public Optional<Category> getCategory(long id) {
        return categoryRepository.findById(id);
    }
}
