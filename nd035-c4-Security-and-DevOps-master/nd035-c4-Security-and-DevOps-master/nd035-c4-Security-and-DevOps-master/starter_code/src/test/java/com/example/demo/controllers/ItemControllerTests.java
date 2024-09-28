package com.example.demo.controllers;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.security.WebSecurityConfig;
import com.example.demo.security.jwt.JwtTokenUtils;
import com.example.demo.security.services.UserDetailsServiceImpl;
import com.example.demo.services.ItemService;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@EnableWebMvc
@WebAppConfiguration
@SpringBootTest(classes = ItemController.class)
@ContextConfiguration(classes = {WebSecurityConfig.class, UserDetailsServiceImpl.class, BCryptPasswordEncoder.class, JwtTokenUtils.class})
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetItems() throws Exception {
        List<Item> expectedItems = Arrays.asList(
                new Item(1L, "Item1", BigDecimal.TEN, "Description1"),
                new Item(2L, "Item2", BigDecimal.TEN, "Description2")
        );
        given(itemService.getAllItems()).willReturn(expectedItems);

        mockMvc.perform(get("/api/item"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("Item1"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("Item2"));
    }

    private static final String GET_ITEM_BY_ID_URL = "/api/item/{id}";

    @Test
    public void testGetItemById_Success() throws Exception {
        Item expectedItem = new Item(1L, "Item1", BigDecimal.TEN, "Description1");
        given(itemService.getItemById(anyLong())).willReturn(expectedItem);

        mockMvc.perform(get(GET_ITEM_BY_ID_URL, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(expectedItem.getId()))
                .andExpect(jsonPath("$.data.name").value(expectedItem.getName()))
                .andExpect(jsonPath("$.data.price").value(expectedItem.getPrice()))
                .andExpect(jsonPath("$.data.description").value(expectedItem.getDescription()));
    }

    @Test
    public void testGetItemById_NotFound() throws Exception {
        String expectedMessage = "Item not found";
        given(itemService.getItemById(anyLong())).willThrow(new ItemNotFoundException(expectedMessage));

        mockMvc.perform(get(GET_ITEM_BY_ID_URL, 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    private static final String GET_ITEMS_BY_NAME_URL = "/api/item/name/{name}";

    @Test
    public void testGetItemsByName_Success() throws Exception {
        List<Item> expectedItems = Arrays.asList(
                new Item(1L, "Item1", BigDecimal.TEN, "Description1"),
                new Item(2L, "Item1", BigDecimal.ONE, "Description2")
        );
        given(itemService.getItemsByName(anyString())).willReturn(expectedItems);

        mockMvc.perform(get(GET_ITEMS_BY_NAME_URL, "Item1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$.data[0].id").value(expectedItems.get(0).getId()))
                .andExpect(jsonPath("$.data[0].name").value(expectedItems.get(0).getName()))
                .andExpect(jsonPath("$.data[0].price").value(expectedItems.get(0).getPrice()))
                .andExpect(jsonPath("$.data[0].description").value(expectedItems.get(0).getDescription()))
                .andExpect(jsonPath("$.data[1].id").value(expectedItems.get(1).getId()))
                .andExpect(jsonPath("$.data[1].name").value(expectedItems.get(1).getName()))
                .andExpect(jsonPath("$.data[1].price").value(expectedItems.get(1).getPrice()))
                .andExpect(jsonPath("$.data[1].description").value(expectedItems.get(1).getDescription()));
    }

    @Test
    public void testGetItemsByName_NotFound() throws Exception {
        String expectedMessage = "Items not found";
        given(itemService.getItemsByName(anyString())).willThrow(new ItemNotFoundException(expectedMessage));

        mockMvc.perform(get(GET_ITEMS_BY_NAME_URL, "Item1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}
