package com.example.demo.services;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Item item;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("testuser");
        cart = new Cart();
        item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.TEN);
        user.setCart(cart);
    }

    @Test
    public void testAddToCart_Success() {
        // Tạo request cho quá trình thêm sản phẩm vào giỏ hàng
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(2);

        // Mock các repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Gọi service để thực hiện thêm sản phẩm vào giỏ hàng
        Cart updatedCart = cartService.addToCart(request);

        // Kiểm tra kết quả
        assertNotNull(updatedCart);
        assertEquals(2, updatedCart.getItems().size());

        // Kiểm tra xem repository được gọi đúng số lần không
        verify(cartRepository, times(1)).save(cart);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(itemRepository, times(1)).findById(1L);
    }

    private static final String UNKNOWN_USERNAME = "unknownuser";

    @Test
    public void testAddToCart_UserNotFound() {
        // Tạo request
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(UNKNOWN_USERNAME);
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(UNKNOWN_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            cartService.addToCart(request);
        });

        verify(cartRepository, times(0)).save(any(Cart.class));
        verify(userRepository, times(1)).findByUsername(UNKNOWN_USERNAME);
    }

    private static final String TEST_USERNAME = "testuser";
    @Test
    public void testAddToCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(TEST_USERNAME);
        request.setItemId(999L);  // Item không tồn tại
        request.setQuantity(2);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> cartService.addToCart(request));

        verify(cartRepository, times(0)).save(any(Cart.class));
        verify(itemRepository, times(1)).findById(999L);

    }

    private static final Long ITEM_ID = 1L;

    @Test
    public void testRemoveFromCart_Success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(TEST_USERNAME);
        request.setItemId(ITEM_ID);
        request.setQuantity(1);

        cart.addItem(item);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart updatedCart = cartService.removeFromCart(request);

        assertAll(
                () -> assertNotNull(updatedCart),
                () -> assertEquals(0, updatedCart.getItems().size())
        );

        verify(cartRepository, times(1)).save(cart);
    }

}
