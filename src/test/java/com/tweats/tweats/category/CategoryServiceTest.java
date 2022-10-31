package com.tweats.tweats.category;

import com.tweats.tweats.image.Image;
import com.tweats.tweats.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup()
    {
        categoryRepository=mock(CategoryRepository.class);
    }

    @Test
    public void shouldBeAbleToSaveCategoryWhenValidDetailsAreProvided() {
        Image image=mock(Image.class);
        User user = mock(User.class);
        Category category = new Category("Juice",image,true,user);
        CategoryService categoryService = new CategoryService(categoryRepository);
        categoryService.save(category);
        verify(categoryRepository).save(any());
    }

    @Test
    public void shouldBeAbleToFetchCategoryWhenIdIsProvided() {
        long id=1;
        Category category = new Category();
        when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));
        CategoryService categoryService = new CategoryService(categoryRepository);

        Optional<Category> fetchedCategory = categoryService.getCategory(id);

        assertThat(Optional.of(category),is(fetchedCategory));
    }
}
