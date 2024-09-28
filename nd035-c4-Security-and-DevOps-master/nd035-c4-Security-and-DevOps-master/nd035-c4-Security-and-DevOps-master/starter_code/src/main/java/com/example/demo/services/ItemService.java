package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        Optional<Item> itemById = itemRepository.findById(id);
        if (itemById.isEmpty()) {
            log.error("[ItemService] Item with id {} not found", id);
            throw new ItemNotFoundException("Item with id " + id + " not found");
        }
        log.info("[ItemService] Found item ID {}", id);
        return itemById.get();
    }

    public List<Item> getItemsByName(String name) {
        List<Item> items = itemRepository.findByName(name);
        if (items == null || items.isEmpty()) {
            log.error("[ItemService] Item with name {} not found", name);
            throw new ItemNotFoundException("Item with name " + name + " not found");
        }
        log.info("[ItemService] Found total {} items with name {}", items.size(), name);
        return items;
    }
}
