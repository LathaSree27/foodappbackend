package com.tweats.service;

import com.tweats.model.Category;
import com.tweats.model.Image;
import com.tweats.model.Item;
import com.tweats.model.User;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

    @Test
    void shouldBeAbleToFetchAllTheItemsWhenAValidCategoryIdIsGiven() {
        List<Item> listOfFetchedItems = new ArrayList<>();
        Image itemImage = new Image();
        Long category_id = 2L;
        Image categoryImage = new Image();
        Category category = new Category("Juice", categoryImage, true, new User());
        listOfFetchedItems.add(new Item("Mango", itemImage,new BigDecimal(80),  category));
        itemService = new ItemService(itemRepository,categoryRepository);

        when(itemRepository.findByCategory_id(category_id)).thenReturn(listOfFetchedItems);

        assertThat(listOfFetchedItems, is(itemService.get(category_id)));

    }
}
