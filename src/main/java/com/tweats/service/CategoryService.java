package com.tweats.service;

import com.tweats.controller.response.CategoryResponse;
import com.tweats.controller.response.VendorCategoryResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@AllArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;
    private UserPrincipalService userPrincipalService;
    private ImageService imageService;

    public void save(String name, MultipartFile categoryImageFile, String email) throws IOException, NotAnImageException, UserNotFoundException, NotAVendorException, CategoryAlreadyAssignedException, ImageSizeExceededException {
        User vendor = userPrincipalService.findUserByEmail(email);
        if (!userPrincipalService.isVendor(vendor)) throw new NotAVendorException();
        Optional<Category> optionalCategory = categoryRepository.findByUserId(vendor.getId());
        if (optionalCategory.isPresent()) throw new CategoryAlreadyAssignedException();
        Image categoryImage = imageService.save(categoryImageFile);
        Category category = new Category(name, categoryImage, vendor);
        categoryRepository.save(category);
    }

    public Category getCategory(String userEmail) throws NoCategoryFoundException, UserNotFoundException {
        User user = userPrincipalService.findUserByEmail(userEmail);
        return categoryRepository.findByUserId(user.getId()).orElseThrow(NoCategoryFoundException::new);
    }

    public VendorCategoryResponse getVendorCategoryResponse(String userEmail) throws UserNotFoundException, NoCategoryFoundException {
        Category category = getCategory(userEmail);
        return new VendorCategoryResponse(category.getId());
    }

    public List<CategoryResponse> getAllCategories() throws NoCategoryFoundException {
        ArrayList<CategoryResponse> categoryResponses = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        if (isEmpty(categories)) throw new NoCategoryFoundException();
        for (Category category : categories) {
            categoryResponses.add(getCategoryResponse(category));
        }
        return categoryResponses;
    }

    private boolean isEmpty(List<Category> categories) {
        return categories.size() == 0;
    }

    private CategoryResponse getCategoryResponse(Category category) {
        return CategoryResponse
                .builder()
                .id(category.getId())
                .categoryName(category.getName())
                .imageLink(imageService.getImageLink(category.getImage()))
                .isOpen(category.isOpen())
                .build();
    }

    public void updateOpenStatus(String vendorEmail) throws UserNotFoundException, NoCategoryFoundException {
        Category category = getCategory(vendorEmail);
        category.setOpen(!category.isOpen());
    }
}
