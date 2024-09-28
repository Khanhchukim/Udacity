package com.example.demo.services;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public Cart addToCart(ModifyCartRequest request) {
        Cart cart = this.getCart(request);
        Item item = getItem(request);
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item));
        cartRepository.save(cart);
        log.info("[CartService] Added {} items to cart", request.getQuantity());
        return cart;
    }

    @Transactional
    public Cart removeFromCart(ModifyCartRequest request) {
        Cart cart = this.getCart(request);
        Item item = getItem(request);
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item));
        cartRepository.save(cart);
        log.info("[CartService] Removed {} items from cart", request.getQuantity());
        return cart;
    }

    private Cart getCart(ModifyCartRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            log.error("[CartService] User not found: {}",request.getUsername());
            throw new UserNotFoundException("User not found: " + request.getUsername());
        }
        log.info("[CartService] Get cart for user: {}", request.getUsername());
        return user.get().getCart();
    }

    private Item getItem(ModifyCartRequest request) {
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (item.isEmpty()) {
            log.error("[CartService] Item not found: ID={}",request.getItemId());
            throw new ItemNotFoundException("Item not found: " + request.getItemId());
        }
        log.info("[CartService] Get item for cart: ID={}", request.getItemId());
        return item.get();
    }
}
