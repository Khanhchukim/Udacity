package com.example.demo.controllers;

import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.exceptions.UserPasswordException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.responses.BaseResponse;
import com.example.demo.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<User>> findById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            BaseResponse<User> response = new BaseResponse<>(true, user);
            return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với User
        } catch (UserNotFoundException e) {
            BaseResponse<User> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về 404 Not Found nếu người dùng không tồn tại
        }
    }

    @GetMapping(value ="/{username}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<User>> findByUserName(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            BaseResponse<User> response = new BaseResponse<>(true, user);
            return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với User
        } catch (UserNotFoundException e) {
            BaseResponse<User> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // Trả về 404 Not Found nếu người dùng không tồn tại
        }
    }

    @PostMapping(value ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<User>> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = userService.createUser(createUserRequest);
            BaseResponse<User> response = new BaseResponse<>(true, user);
            return ResponseEntity.ok(response);  // Trả về phản hồi 200 OK với User
        } catch (UserAlreadyExistsException e) {
            BaseResponse<User> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);  // Trả về 409 Conflict nếu người dùng đã tồn tại
        } catch (UserPasswordException e) {
            BaseResponse<User> errorResponse = new BaseResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);  // Trả về 400 Bad Request nếu mật khẩu không hợp lệ
        }
    }
}
