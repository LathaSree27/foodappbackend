package com.tweats.controller;

import com.tweats.controller.response.CategoryResponse;
import com.tweats.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void shouldBeAbleToReturnAllCategoriesWhenUserLoggedIn() {
        List<CategoryResponse> expectedCategoryResponses = new ArrayList<>();
        List<CategoryResponse> actualCategoryResponses = categoryController.fetchAllCategories();

        verify(categoryService).getAllCategories();
        assertThat(actualCategoryResponses, is(expectedCategoryResponses));
    }
}
