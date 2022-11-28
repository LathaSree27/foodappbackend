package com.tweats.controller;

import com.tweats.controller.response.CategoryResponse;
import com.tweats.exceptions.*;
import com.tweats.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {


    @Mock
    CategoryService categoryService;

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
    void shouldBeAbleToReturnAllCategoriesWhenUserLoggedIn() throws NoCategoryFoundException {
        List<CategoryResponse> expectedCategoryResponses = new ArrayList<>();
        List<CategoryResponse> actualCategoryResponses = categoryController.fetchAllCategories();

        verify(categoryService).getAllCategories();
        assertThat(actualCategoryResponses, is(expectedCategoryResponses));
    }


}
