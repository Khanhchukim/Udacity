package com.example.demo.services;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private UserOrder userOrder;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testUser");

        Cart cart = new Cart();
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(3));
        cart.addItem(item);
        user.setCart(cart);

        userOrder = new UserOrder();
        userOrder.setUser(user);
    }

    @Test
    public void submitOrder_UserExists_OrderSaved() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(UserOrder.class))).thenReturn(userOrder);

        UserOrder result = orderService.submitOrder("testUser");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(user, result.getUser())
        );

        verify(orderRepository, times(1)).save(any(UserOrder.class));

    }

    @Test
    public void submitOrder_UserDoesNotExist_ThrowsUserNotFoundException() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderService.submitOrder("unknownUser"));
    }

    @Test
    public void getOrdersForUser_UserExists_ReturnsOrders() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(userOrder));

        List<UserOrder> result = orderService.getOrdersForUser("testUser");

        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(userOrder, result.get(0))
        );

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(orderRepository, times(1)).findByUser(user);

    }

    @Test
    public void getOrdersForUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderService.getOrdersForUser("unknownUser"));
    }
}
