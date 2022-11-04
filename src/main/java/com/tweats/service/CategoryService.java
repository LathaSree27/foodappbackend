package com.tweats.service;

import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private UserPrincipalService userPrincipalService;

    public void save(String name, Image categoryImage, String email) {
        Category category = new Category(name, categoryImage, userRepository.findByEmail(email).get());
        categoryRepository.save(category);
    }

    public Category getCategory(String userEmail) throws NoCategoryFoundException {

        User user = userPrincipalService.findUserByEmail(userEmail);

        Category category = categoryRepository.findByUser_id(user.getId());
        if (category == null) {
            throw new NoCategoryFoundException();
        }

        return category;
    }
}
