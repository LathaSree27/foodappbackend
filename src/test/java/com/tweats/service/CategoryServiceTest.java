package com.tweats.service;

import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.NotAVendorException;
import com.tweats.exceptions.NotAnImageException;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {

    private CategoryRepository categoryRepository;

    private UserPrincipalService userPrincipalService;
    private ImageService imageService;
    private CategoryService categoryService;
    private User user;
    private Image image;


    @BeforeEach
    public void setup() {
        categoryRepository = mock(CategoryRepository.class);
        imageService = mock(ImageService.class);
        userPrincipalService = mock(UserPrincipalService.class);
        categoryService = new CategoryService(categoryRepository, userPrincipalService, imageService);
        user = mock(User.class);
        image = mock(Image.class);
    }

    @Test
    public void shouldBeAbleToSaveValidCategoryWhenCategoryDetailsAreProvided() throws IOException, NotAnImageException, UserNotFoundException, NotAVendorException {
        String email = "user@gmail.com";
        MockMultipartFile categoryImageFile = new MockMultipartFile("image.png", "Hello".getBytes());
        when(userPrincipalService.findUserByEmail(email)).thenReturn(user);
        when(imageService.save(categoryImageFile)).thenReturn(image);
        String categoryName = "Juice";
        Category category = new Category(categoryName, image, user);

        categoryService.save(categoryName, categoryImageFile, email);

        verify(categoryRepository).save(category);
    }

    @Test
    public void shouldBeAbleToFetchCategoryWhenUserIdIsProvided() throws NoCategoryFoundException, UserNotFoundException {
        String userEmail = "abc@example.com";
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);
        long userId = user.getId();
        Category category = new Category();
        when(categoryRepository.findByUser_id(userId)).thenReturn(Optional.of(category));

        Category fetchedCategory = categoryService.getCategory(userEmail);

        assertThat(category, is(fetchedCategory));
    }

    @Test
    void shouldNotBeAbleToFetchCategoryWhenCategoryIsNotPresentForTheUser() throws UserNotFoundException {
        String userEmail = "abc@example.com";
        when(userPrincipalService.findUserByEmail(userEmail)).thenReturn(user);

        assertThrows(NoCategoryFoundException.class, () -> categoryService.getCategory(userEmail));

    }
}
