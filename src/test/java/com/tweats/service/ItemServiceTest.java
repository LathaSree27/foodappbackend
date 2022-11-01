package com.tweats.service;

import com.tweats.model.Category;
import com.tweats.repo.CategoryRepository;
import com.tweats.model.Image;
import com.tweats.model.User;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;
    private ItemService itemService;


    @BeforeEach
    public void setUp() {
        itemRepository = mock(ItemRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        itemRepository.deleteAll();
    }

    @AfterEach
    public void after() {
        itemRepository.deleteAll();
    }

    @Test
    void shouldBeAbleToAddItemWhenItemIsGiven() {
        Image itemImage = new Image();
        Image categoryImage = new Image();
        Long category_id = 1L;
        Category category = new Category("Juice", categoryImage, true, new User());
        when(categoryRepository.findById(category_id)).thenReturn(Optional.of(category));
        itemService = new ItemService(itemRepository,categoryRepository);

        itemService.save("Mango", new BigDecimal(80), itemImage, category_id);

        verify(itemRepository).save(any());
    }
}
