package com.tweats.controller;

import com.tweats.controller.response.CategoryResponse;
import com.tweats.controller.response.VendorCategoryResponse;
import com.tweats.exceptions.*;
import com.tweats.model.Category;
import com.tweats.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {


    @Mock
    CategoryService categoryService;

    @Mock
    Principal principal;

    @Mock
    Category category;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void shouldBeAbleToSaveCategoryWhenNameFileAndUseremailIsGiven() throws UserNotFoundException, ImageSizeExceededException, IOException, NotAVendorException, CategoryAlreadyAssignedException, NotAnImageException {
        MockMultipartFile categoryImageFile = new MockMultipartFile("image.png", "Hello".getBytes());
        String name = "juice";
        String user_email = "abc@gmail.com";

        categoryController.save(categoryImageFile,name,user_email);

        verify(categoryService).save(name,categoryImageFile,user_email);

    }

    @Test
    void shouldBeAbleToFetchCategoryOfLoggedInVendor() throws UserNotFoundException, NoCategoryFoundException {
        when(categoryService.getCategory(principal.getName())).thenReturn(category);
        VendorCategoryResponse expectedVendorCategoryResponse = new VendorCategoryResponse(category.getId());

        assertThat(categoryController.fetchCategory(principal),is(expectedVendorCategoryResponse));
    }

    @Test
    void shouldBeAbleToReturnAllCategoriesWhenUserLoggedIn() throws NoCategoryFoundException {
        List<CategoryResponse> expectedCategoryResponses = new ArrayList<>();
        List<CategoryResponse> actualCategoryResponses = categoryController.fetchAllCategories();

        verify(categoryService).getAllCategories();
        assertThat(actualCategoryResponses, is(expectedCategoryResponses));
    }


}
