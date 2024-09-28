package com.example.demo.controllers;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.responses.BaseResponse;
import com.example.demo.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/submit/{username}")
	public ResponseEntity<BaseResponse<UserOrder>> submit(@PathVariable String username) {
		try {
			UserOrder order = orderService.submitOrder(username);
			BaseResponse<UserOrder> response = new BaseResponse<>(true, order);
			return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với đơn hàng
		} catch (UserNotFoundException e) {
			BaseResponse<UserOrder> errorResponse = new BaseResponse<>(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về 404 Not Found nếu người dùng không tồn tại
		}
	}

	@GetMapping("/history/{username}")
	public ResponseEntity<BaseResponse<List<UserOrder>>> getOrdersForUser(@PathVariable String username) {
		try {
			List<UserOrder> orders = orderService.getOrdersForUser(username);
			BaseResponse<List<UserOrder>> response = new BaseResponse<>(true, orders);
			return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với danh sách đơn hàng
		} catch (UserNotFoundException e) {
			BaseResponse<List<UserOrder>> errorResponse = new BaseResponse<>(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về 404 Not Found nếu người dùng không tồn tại
		}
	}
}
