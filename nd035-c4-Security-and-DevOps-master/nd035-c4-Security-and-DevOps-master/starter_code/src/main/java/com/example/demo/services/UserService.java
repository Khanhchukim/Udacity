package com.example.demo.services;

import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.exceptions.UserPasswordException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, CartRepository cartRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findById(Long id) {
        Optional<User> userById = userRepository.findById(id);
        if (userById.isEmpty()) {
            log.error("[UserService] User with ID {} not found", id);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        log.info("[UserService] Found user: ID={}", id);
        return userById.get();

    }

    public User findByUsername(String username) {
        Optional<User> userByUsername = userRepository.findByUsername(username);
        if (userByUsername.isEmpty()) {
            log.error("[UserService] User with username {} not found", username);
            throw new UserNotFoundException("User with username " + username + " not found");
        }
        log.info("[UserService] Found user: username={}", username);
        return userByUsername.get();
    }

    public User createUser(CreateUserRequest createUserRequest) {
        Optional<User> userByUsername = userRepository.findByUsername(createUserRequest.getUsername());
        if (userByUsername.isPresent()) {
            log.error("[UserService] User with username {} already exists", createUserRequest.getUsername());
            throw new UserAlreadyExistsException("User with username " + createUserRequest.getUsername() + " already exists");
        }
        User user = new User();
        user.setUsername(createUserRequest.getUsername());

        if (createUserRequest.getPassword() == null || createUserRequest.getPassword().isEmpty()) {
            log.error("[UserService] You must enter your password to create a new user");
            throw new UserPasswordException("You must enter your password to create a new user");
        }

        if (createUserRequest.getPassword().length() < 8) {
            log.error("Password must be at least 8 characters");
            throw new UserPasswordException("Password must be at least 8 characters");
        }

        if (!Objects.equals(createUserRequest.getPassword(), createUserRequest.getConfirmPassword())) {
            log.error("Passwords do not match");
            throw new UserPasswordException("Passwords do not match");
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        userRepository.save(user);
        log.info("[UserService] User created: {}", user.getUsername());
        return user;
    }
}
