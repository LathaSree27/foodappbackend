package com.tweats.service;

import com.tweats.controller.response.CategoryResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserPrincipalService userPrincipalService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private User user;
    @Mock
    private Image image;

    @Mock
    private Category category;
    private String userEmail;


    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() throws IOException, NotAnImageException, UserNotFoundException, NotAVendorException, CategoryAlreadyAssignedException {
        String categoryName = "Juice";
        userEmail = "abc@example.com";
        Category category = new Category(categoryName, image, user);
        MockMultipartFile categoryImageFile = new MockMultipartFile("image.png", "Hello".getBytes());
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(userPrincipalService.isVendor(user)).thenReturn(true);
        when(imageService.save(categoryImageFile)).thenReturn(image);

        categoryService.save(categoryName, categoryImageFile, userEmail);

        verify(categoryRepository).save(category);
    }

    @Test
    public void shouldBeAbleToFetchCategoryWhenUserIdIsProvided() throws NoCategoryFoundException, UserNotFoundException {
        long userId = user.getId();
        userEmail = "abc@example.com";
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByUser_id(userId)).thenReturn(Optional.of(category));

        Category fetchedCategory = categoryService.getCategory(userEmail);

        assertThat(category, is(fetchedCategory));
    }

    @Test
    void shouldThrowNoCategoryFoundExceptionWhenCategoryIsNotPresentForTheUser() throws UserNotFoundException {
        userEmail = "abc@example.com";
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);

        assertThrows(NoCategoryFoundException.class, () -> categoryService.getCategory(userEmail));
    }

    @Test
    public void shouldThrowNotAVendorExceptionWhenTheUserIsNotAVendor() throws UserNotFoundException {
        userEmail = "abc@example.com";
        MockMultipartFile categoryImageFile = new MockMultipartFile("image.png", "Hello".getBytes());
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(userPrincipalService.isVendor(user)).thenReturn(false);

        assertThrows(NotAVendorException.class, () -> categoryService.save(category.getName(), categoryImageFile, userEmail));
    }

    @Test
    void shouldThrowCategoryAlreadyAssignedExceptionWhenTheGivenVendorHasCategoryAssigned() throws UserNotFoundException {
        long userId = 2;
        userEmail = "abc@example.com";
        MockMultipartFile categoryImageFile = new MockMultipartFile("image.png", "Hello".getBytes());
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(userPrincipalService.isVendor(user)).thenReturn(true);
        when(user.getId()).thenReturn(userId);
        when(categoryRepository.findByUser_id(userId)).thenReturn(Optional.of(category));

        assertThrows(CategoryAlreadyAssignedException.class, () -> categoryService.save(category.getName(), categoryImageFile, userEmail));
    }

    @Test
    void shouldBeAbleToGetAllCategories() {
        String categoryName = "Juice";
        when(category.getImage()).thenReturn(image);
        when(category.getName()).thenReturn(categoryName);
        List<CategoryResponse> expectedCategories = new ArrayList<>(Arrays.asList(CategoryResponse
                .builder()
                .id(category.getId())
                .categoryName(category.getName())
                .imageLink("http://localhost:8080/tweats/api/v1/images/" + category.getImage().getId())
                .isOpen(category.isOpen())
                .build()));
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(category)));

        List<CategoryResponse> actualCategories = categoryService.getAllCategories();

        verify(categoryRepository).findAll();
        assertThat(actualCategories,is(expectedCategories));

    }
}
