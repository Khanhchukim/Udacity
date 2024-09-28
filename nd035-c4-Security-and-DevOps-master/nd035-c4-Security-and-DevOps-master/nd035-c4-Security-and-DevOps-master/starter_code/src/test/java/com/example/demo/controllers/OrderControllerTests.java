package com.example.demo.controllers;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.security.WebSecurityConfig;
import com.example.demo.security.jwt.JwtTokenUtils;
import com.example.demo.security.services.UserDetailsServiceImpl;
import com.example.demo.services.ItemService;
import com.example.demo.services.OrderService;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@EnableWebMvc
@WebAppConfiguration
@SpringBootTest(classes = OrderController.class)
@ContextConfiguration(classes = {WebSecurityConfig.class, UserDetailsServiceImpl.class, BCryptPasswordEncoder.class, JwtTokenUtils.class})
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    private static final String SUBMIT_ORDER_URL = "/api/order/submit/{username}";
    private static final String GET_ORDER_HISTORY_URL = "/api/order/history/{username}";

    @Test
    void testSubmit() throws Exception {
        UserOrder userOrder = new UserOrder();
        Mockito.when(orderService.submitOrder("john")).thenReturn(userOrder);

        mockMvc.perform(MockMvcRequestBuilders.post(SUBMIT_ORDER_URL, "john")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testSubmitUserNotFound() throws Exception {
        String expectedMessage = "User not found";
        Mockito.when(orderService.submitOrder("unknownUser"))
                .thenThrow(new UserNotFoundException(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.post(SUBMIT_ORDER_URL, "unknownUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void testGetOrdersForUser() throws Exception {
        UserOrder userOrder = new UserOrder();
        Mockito.when(orderService.getOrdersForUser("testUser")).thenReturn(Collections.singletonList(userOrder));

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ORDER_HISTORY_URL, "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));
    }

    @Test
    void testGetOrdersForUserNotFound() throws Exception {
        String expectedMessage = "User not found";
        Mockito.when(orderService.getOrdersForUser("unknownUser"))
                .thenThrow(new UserNotFoundException(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ORDER_HISTORY_URL, "unknownUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage));
    }
}
