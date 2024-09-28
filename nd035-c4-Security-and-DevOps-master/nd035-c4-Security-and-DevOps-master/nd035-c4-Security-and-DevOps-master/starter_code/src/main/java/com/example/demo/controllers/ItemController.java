package com.example.demo.controllers;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.responses.BaseResponse;
import com.example.demo.services.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Item>>> getItems() {
        return ResponseEntity.ok(new BaseResponse<>(true, itemService.getAllItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Item>> getItemById(@PathVariable Long id) {
        try {
            Item itemById = itemService.getItemById(id);
            BaseResponse<Item> response = new BaseResponse<>(true, itemById);
            return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với Item
        } catch (ItemNotFoundException e) {
            BaseResponse<Item> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về phản hồi 404 Not Found với thông báo lỗi
        }

    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BaseResponse<List<Item>>> getItemsByName(@PathVariable String name) {
        try {
            List<Item> itemsByName = itemService.getItemsByName(name);
            BaseResponse<List<Item>> response = new BaseResponse<>(true, itemsByName);
            return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với danh sách Item
        } catch (ItemNotFoundException e) {
            BaseResponse<List<Item>> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về 404 Not Found với thông báo lỗi
        }
    }
}
