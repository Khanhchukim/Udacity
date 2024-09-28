package com.example.demo.controllers;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.security.WebSecurityConfig;
import com.example.demo.security.jwt.JwtTokenUtils;
import com.example.demo.security.services.UserDetailsServiceImpl;
import com.example.demo.services.CartService;
import com.example.demo.services.ItemService;
import com.example.demo.services.UserService;
import org.apache.commons.codec.CharEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CartController.class)
@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = {WebSecurityConfig.class, UserDetailsServiceImpl.class, BCryptPasswordEncoder.class, JwtTokenUtils.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
public class CartControllerTests {
    @Autowired
    private JacksonTester<ModifyCartRequest> json;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    private ModifyCartRequest request;
    private Cart cart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(10);
        request.setUsername("testUser");

        cart = new Cart();
        cart.setId(1L);
        cart.addItem(new Item(1L, "Test Item 1", BigDecimal.TEN, "Description"));
    }

    private static final String ADD_TO_CART_URL = "/api/cart/addToCart";
    private static final String REMOVE_FROM_CART_URL = "/api/cart/removeFromCart";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;

    private ResultActions performAddToCartRequest(ModifyCartRequest request) throws Exception {
        return mockMvc.perform(post(ADD_TO_CART_URL)
                .content(json.write(request).getJson())
                .characterEncoding(CharEncoding.UTF_8)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));
    }

    private ResultActions performRemoveFromCartRequest(ModifyCartRequest request) throws Exception {
        return mockMvc.perform(post(REMOVE_FROM_CART_URL)
                .content(json.write(request).getJson())
                .characterEncoding(CharEncoding.UTF_8)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));
    }

    @Test
    public void testAddToCart() throws Exception {
        when(cartService.addToCart(any(ModifyCartRequest.class))).thenReturn(cart);

        performAddToCartRequest(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.items.length()").value(cart.getItems().size()))
                .andExpect(jsonPath("$.data.total").value(cart.getTotal()));
    }


    @Test
    public void testAddToCart_UserNotFoundException() throws Exception {
        String expectedMessage = "User not found: " + request.getUsername();
        when(cartService.addToCart(any(ModifyCartRequest.class)))
                .thenThrow(new UserNotFoundException(expectedMessage));

        performAddToCartRequest(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    public void testAddToCart_ItemNotFoundException() throws Exception {
        String expectedMessage = "Item not found: " + request.getItemId();
        when(cartService.addToCart(any(ModifyCartRequest.class)))
                .thenThrow(new ItemNotFoundException(expectedMessage));

        performAddToCartRequest(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }


    @Test
    public void testRemoveFromCart() throws Exception {
        when(cartService.removeFromCart(any(ModifyCartRequest.class))).thenReturn(cart);

        performRemoveFromCartRequest(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.items.length()").value(cart.getItems().size()))
                .andExpect(jsonPath("$.data.total").value(cart.getTotal()));
    }


    @Test
    public void testRemoveFromCart_UserNotFoundException() throws Exception {
        String expectedMessage = "User not found: " + request.getUsername();
        when(cartService.removeFromCart(any(ModifyCartRequest.class)))
                .thenThrow(new UserNotFoundException(expectedMessage));

        performRemoveFromCartRequest(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    public void testRemoveFromCart_ItemNotFoundException() throws Exception {
        String expectedMessage = "Item not found: " + request.getItemId();
        when(cartService.removeFromCart(any(ModifyCartRequest.class)))
                .thenThrow(new ItemNotFoundException(expectedMessage));

        performRemoveFromCartRequest(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

}
