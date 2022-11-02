package com.tweats.service;

import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
    }

    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() {
        Image image = mock(Image.class);
        User user = mock(User.class);
        String email = "user@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        CategoryService categoryService = new CategoryService(categoryRepository, userRepository);

        categoryService.save("Juice", image, true, email);

        verify(categoryRepository).save(any());
    }

    @Test
    public void shouldBeAbleToFetchCategoryWhenIdIsProvided() {
        long id = 1;
        Category category = new Category();
        when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));
        CategoryService categoryService = new CategoryService(categoryRepository, userRepository);

        Optional<Category> fetchedCategory = categoryService.getCategory(id);

        assertThat(Optional.of(category), is(fetchedCategory));
    }
}
