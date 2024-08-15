package com.example.demo.Service.impl;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StudentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserSuccess() {
        UserDto userDto = new UserDto("test@example.com", "password", "mentor");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        assertDoesNotThrow(() -> studentService.createUser(userDto));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUserAlreadyExists() {
        User existingUser = new User(1L, "test@example.com", "password", "mentor");
        UserDto userDto = new UserDto("test@example.com", "password", "mentor");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        assertThrows(CustomerAlreadyExistsException.class, () -> studentService.createUser(userDto));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFetchDetailsSuccess() {
        User user = new User(1L, "test@example.com", "password", "mentor");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDto userDto = studentService.fetchDetails("test@example.com");

        assertNotNull(userDto);
        assertEquals("test@example.com", userDto.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testFetchDetailsNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.fetchDetails("notfound@example.com"));

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    public void testDeleteUserSuccess() {
        User user = new User(1L, "test@example.com", "password", "mentor");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        boolean isDeleted = studentService.deleteUser("test@example.com");

        assertTrue(isDeleted);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        boolean isDeleted = studentService.deleteUser("notfound@example.com");

        assertFalse(isDeleted);
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    public void testFetchDetailsByRole() {
        User user = new User(1L, "test@example.com", "password", "mentor");
        when(userRepository.findAllByRoleName(anyString())).thenReturn(Collections.singletonList(user));

        var userList = studentService.fetchDetailsByRole("mentor");

        assertNotNull(userList);
        assertEquals(1, userList.size());
        assertEquals("mentor", userList.get(0).getRoleName());
        verify(userRepository, times(1)).findAllByRoleName("mentor");
    }
}
