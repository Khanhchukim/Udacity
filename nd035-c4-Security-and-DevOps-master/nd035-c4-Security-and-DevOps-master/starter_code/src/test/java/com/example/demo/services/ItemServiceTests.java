package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
    }

    @Test
    void testGetAllItems() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item));

        List<Item> items = itemService.getAllItems();

        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item foundItem = itemService.getItemById(1L);

        assertNotNull(foundItem);
        assertEquals("Test Item", foundItem.getName());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItemByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemById(1L);
        });

        assertEquals("Item with id 1 not found", exception.getMessage());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItemsByName() {
        when(itemRepository.findByName("Test Item")).thenReturn(Arrays.asList(item));

        List<Item> items = itemService.getItemsByName("Test Item");

        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository, times(1)).findByName("Test Item");
    }

    @Test
    void testGetItemsByNameNotFound() {
        when(itemRepository.findByName("Nonexistent Item")).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemsByName("Nonexistent Item");
        });

        assertEquals("Item with name Nonexistent Item not found", exception.getMessage());
        verify(itemRepository, times(1)).findByName("Nonexistent Item");
    }
    @Test
    void testGetItemsByNameNotFoundNull() {
        when(itemRepository.findByName("Nonexistent Item")).thenReturn(null);

        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemsByName("Nonexistent Item");
        });

        assertEquals("Item with name Nonexistent Item not found", exception.getMessage());
        verify(itemRepository, times(1)).findByName("Nonexistent Item");
    }
}
