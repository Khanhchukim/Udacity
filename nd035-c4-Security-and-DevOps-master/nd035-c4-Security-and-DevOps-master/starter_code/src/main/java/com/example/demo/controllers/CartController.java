package com.example.demo.controllers;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.model.responses.BaseResponse;
import com.example.demo.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public ResponseEntity<BaseResponse<Cart>> addToCart(@RequestBody ModifyCartRequest request) {
        try {
            Cart cart = cartService.addToCart(request);
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse<>(false, "Failed to add item to cart"));
            }
            return createSuccessResponse(cart);
        } catch (UserNotFoundException | ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, e.getMessage()));
        }
    }

    private ResponseEntity<BaseResponse<Cart>> createSuccessResponse(Cart cart) {
        return ResponseEntity.ok(new BaseResponse<>(true, cart));
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<BaseResponse<Cart>> removeFromCart(@RequestBody ModifyCartRequest request) {
        try {
            Cart cart = cartService.removeFromCart(request);
            BaseResponse<Cart> response = new BaseResponse<>(true, cart);
            return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với Cart
        } catch (UserNotFoundException | ItemNotFoundException e) {
            BaseResponse<Cart> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về phản hồi 404 Not Found với thông báo lỗi
        }
    }
}
