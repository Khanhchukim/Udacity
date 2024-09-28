package com.example.demo.controllers;

import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.exceptions.UserPasswordException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.WebSecurityConfig;
import com.example.demo.security.jwt.JwtTokenUtils;
import com.example.demo.security.services.UserDetailsServiceImpl;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserController.class)
@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = {WebSecurityConfig.class, UserDetailsServiceImpl.class, BCryptPasswordEncoder.class, JwtTokenUtils.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUpForEachTest() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1);
        user.setUsername("testUser");
    }

    private static final String CREATE_USER_URL = "/api/user/create";

    @Test
    public void testCreateUser_ShouldReturnUser() throws Exception {
        // Prepare request
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        // Prepare expected response
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        // Mock service response
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);

        // Perform request and assert response
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_URL)
                        .content(json.write(userRequest).getJson())
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"));
    }


    @Test
    public void testCreateUser_ShouldThrowUserPasswordException_WhenPasswordNull() throws Exception {
        // Prepare request
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword(null);
        userRequest.setConfirmPassword(null);

        // Expected message
        String expectedMessage = "You must enter your password to create a new user";

        // Mock service exception
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new UserPasswordException(expectedMessage));

        // Perform request and assert exception
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_URL)
                        .content(json.write(userRequest).getJson())
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    public void testCreateUser_ShouldThrowUserPasswordException_WhenPasswordEmpty() throws Exception {
        // Prepare request
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("");
        userRequest.setConfirmPassword("");

        // Expected message
        String expectedMessage = "You must enter your password to create a new user";

        // Mock service exception
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new UserPasswordException(expectedMessage));

        // Perform request and assert exception
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_URL)
                        .content(json.write(userRequest).getJson())
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }


    @Test
    public void testCreateUser_ShouldThrowUserPasswordException_WhenPasswordNotMatch() throws Exception {
        // Prepare request
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("differentPassword");

        // Expected message
        String expectedMessage = "Passwords do not match";

        // Mock service exception
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new UserPasswordException(expectedMessage));

        // Perform request and assert exception
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_URL)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content(json.write(userRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    public void testCreateUser_ShouldThrowUserPasswordException_WhenPasswordTooShort() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("short");
        userRequest.setConfirmPassword("short");

        when(userService.createUser(any(CreateUserRequest.class))).thenThrow(new UserPasswordException("Password must be at least 8 characters"));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/user/create")
                                .content(json.write(userRequest).getJson())
                                .characterEncoding(CharEncoding.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters"));
    }

    @Test
    public void testCreateUser_ShouldThrowUserAlreadyExistsException_WhenUserExisted() throws Exception {
        // Prepare request
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        // Expected message
        String expectedMessage = "User with username " + userRequest.getUsername() + " already exists";

        // Mock service exception
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new UserAlreadyExistsException(expectedMessage));

        // Perform request and assert exception
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_URL)
                        .content(json.write(userRequest).getJson())
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }


    private static final String FIND_USER_BY_ID_URL = "/api/user/id/{id}";

    @Test
    public void testFindById_ShouldReturnUser() throws Exception {
        // Prepare expected user
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testUser");

        // Mock service response
        when(userService.findById(1L)).thenReturn(expectedUser);

        // Perform request and assert response
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_USER_BY_ID_URL, 1L)
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"));
    }

    @Test
    public void testFindById_NotFound() throws Exception {
        // Expected message
        String expectedMessage = "User with ID 1 not found";

        // Mock service exception
        when(userService.findById(1L)).thenThrow(new UserNotFoundException(expectedMessage));

        // Perform request and assert exception
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_USER_BY_ID_URL, 1L)
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    private static final String FIND_USER_BY_USERNAME_URL = "/api/user/{username}";

    @Test
    public void testFindByUsername_ShouldReturnUser() throws Exception {
        // Prepare expected user
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testUser");

        // Mock service response
        when(userService.findByUsername("testUser")).thenReturn(expectedUser);

        // Perform request and assert response
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_USER_BY_USERNAME_URL, "testUser")
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"));
    }

    @Test
    public void testFindByUsername_NotFound() throws Exception {
        // Expected message
        String expectedMessage = "User with username testUser not found";

        // Mock service exception
        when(userService.findByUsername("testUser"))
                .thenThrow(new UserNotFoundException(expectedMessage));

        // Perform request and assert exception
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_USER_BY_USERNAME_URL, "testUser")
                        .characterEncoding(CharEncoding.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}