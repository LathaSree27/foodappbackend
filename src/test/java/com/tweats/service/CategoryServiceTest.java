package com.tweats.service;

import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.UserRepository;
import org.junit.jupiter.api.Assertions;
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

    //    @Autowired
    UserPrincipalService userPrincipalService;

    @BeforeEach
    public void setup() {
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        userPrincipalService = mock(UserPrincipalService.class);
    }

    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() {
        Image image = mock(Image.class);
        User user = mock(User.class);
        String email = "user@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        CategoryService categoryService = new CategoryService(categoryRepository, userRepository, userPrincipalService);

        categoryService.save("Juice", image, email);

        verify(categoryRepository).save(any());
    }

    @Test
    public void shouldBeAbleToFetchCategoryWhenUserIdIsProvided() throws NoCategoryFoundException {
        String userEmail = "abc@example.com";
        User user = mock(User.class);
        userRepository.save(user);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        Long userId = user.getId();
        Category category = new Category();
        when(categoryRepository.findByUser_id(userId))
                .thenReturn(category);
        CategoryService categoryService = new CategoryService(categoryRepository, userRepository, userPrincipalService);

        Category fetchedCategory = categoryService.getCategory(userEmail);

        assertThat(category, is(fetchedCategory));
    }

    @Test
    void shouldNotBeAbleToFetchCategoryWhenCategoryIsNotPresentForTheUser() {
        String userEmail = "abc@example.com";
        User user = mock(User.class);
        userRepository.save(user);
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        Long userId = user.getId();
        CategoryService categoryService = new CategoryService(categoryRepository, userRepository, userPrincipalService);

        Assertions.assertThrows(NoCategoryFoundException.class, () -> categoryService.getCategory(userEmail));

    }
}
