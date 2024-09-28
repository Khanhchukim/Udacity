package com.example.demo.services;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final static Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public UserOrder submitOrder(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("[OrderService] User not found: {}", username);
            throw new UserNotFoundException("User not found: " + username);
        }
        UserOrder order = UserOrder.createFromCart(user.get().getCart());
        log.info("[OrderService] Order created: {}", order);
        return orderRepository.save(order);
    }

    public List<UserOrder> getOrdersForUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("[OrderService] User not found: {}", username);
            throw new UserNotFoundException("User not found: " + username);
        }
        log.info("[OrderService] Get orders for user: {}", username);
        return orderRepository.findByUser(user.get());
    }
}
