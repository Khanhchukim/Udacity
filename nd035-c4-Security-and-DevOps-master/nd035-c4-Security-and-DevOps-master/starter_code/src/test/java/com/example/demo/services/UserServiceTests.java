package com.example.demo.services;

import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.exceptions.UserPasswordException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("password123");
        createUserRequest.setConfirmPassword("password123");
    }

    // Test for findById - success case
    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(1L);

        assertAll(
                () -> assertNotNull(foundUser),
                () -> assertEquals(user.getUsername(), foundUser.getUsername())
        );

    }

    // Test for findById - failure case
    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findById(1L));

        assertEquals("User with ID 1 not found", exception.getMessage());

    }

    // Test for findByUsername - success case
    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testUser");

        assertAll(
                () -> assertNotNull(foundUser),
                () -> assertEquals(user.getUsername(), foundUser.getUsername())
        );

    }

    // Test for findByUsername - failure case
    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("testUser"));
    }

    // Test for createUser - success case
    @Test
    void testCreateUser_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPassword");

        User createdUser = userService.createUser(createUserRequest);

        assertAll(
                () -> assertNotNull(createdUser),
                () -> assertEquals("testUser", createdUser.getUsername()),
                () -> assertEquals("encodedPassword", createdUser.getPassword())
        );

        verify(userRepository, times(1)).save(any(User.class));
        verify(cartRepository, times(1)).save(any(Cart.class));

    }

    // Test for createUser - user already exists case
    @Test
    void testCreateUser_UserAlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createUserRequest));
    }

    // Test for createUser - password null case
    @Test
    void testCreateUser_PasswordNull() {
        createUserRequest.setPassword(null);
        assertThrows(UserPasswordException.class, () -> userService.createUser(createUserRequest));
    }
    // Test for createUser - password null case
    @Test
    void testCreateUser_PasswordEmpty() {
        createUserRequest.setPassword("");
        assertThrows(UserPasswordException.class, () -> userService.createUser(createUserRequest));
    }

    // Test for createUser - password too short case
    @Test
    void testCreateUser_PasswordTooShort() {
        String invalidPassword = "short";
        createUserRequest.setPassword(invalidPassword);
        createUserRequest.setConfirmPassword(invalidPassword);

        assertThrows(UserPasswordException.class, () -> userService.createUser(createUserRequest));

    }

    // Test for createUser - passwords do not match case
    @Test
    void testCreateUser_PasswordsDoNotMatch() {
        createUserRequest.setPassword("password123");
        createUserRequest.setConfirmPassword("differentPassword");
        assertThrows(UserPasswordException.class, () -> userService.createUser(createUserRequest));
    }
}
