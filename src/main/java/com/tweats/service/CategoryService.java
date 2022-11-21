package com.tweats.service;

import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.NotAVendorException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@Service
@Transactional
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private UserPrincipalService userPrincipalService;

    private ImageService imageService;

    public void save(String name, MultipartFile categoryImageFile, String email) throws IOException, NotAnImageException, UserNotFoundException, NotAVendorException {
        User vendor = userPrincipalService.findUserByEmail(email);
        if (!userPrincipalService.isVendor(vendor))
            throw new NotAVendorException();
        Image categoryImage = imageService.save(categoryImageFile);

        Category category = new Category(name, categoryImage, vendor);

        categoryRepository.save(category);
    }

    public Category getCategory(String userEmail) throws NoCategoryFoundException, UserNotFoundException {
        User user = userPrincipalService.findUserByEmail(userEmail);

        Optional<Category> optionalCategory = categoryRepository.findByUser_id(user.getId());
        if (optionalCategory.isPresent()) return optionalCategory.get();

        throw new NoCategoryFoundException();
    }
}
