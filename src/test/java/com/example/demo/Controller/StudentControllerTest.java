package com.example.demo.controller;

import com.example.demo.dto.ResponseDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.service.IStudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStudentService studentService;

    @Test
    public void testCreateUser() throws Exception {
        UserDto userDto = new UserDto("test@example.com", "password", "mentor");

        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"roleName\":\"mentor\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Successfully created"));

        verify(studentService, times(1)).createUser(eq(userDto));
    }

    @Test
    public void testCreateUserAlreadyExists() throws Exception {
        UserDto userDto = new UserDto("test@example.com", "password", "mentor");
        doThrow(new CustomerAlreadyExistsException("User already exist for given mail test@example.com"))
                .when(studentService).createUser(eq(userDto));

        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"roleName\":\"mentor\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User already exist for given mail test@example.com"));

        verify(studentService, times(1)).createUser(eq(userDto));
    }

    @Test
    public void testFetchStudentDetails() throws Exception {
        UserDto userDto = new UserDto("test@example.com", "password", "mentor");

        when(studentService.fetchDetails(anyString())).thenReturn(userDto);

        mockMvc.perform(get("/api/fetch")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(studentService, times(1)).fetchDetails("test@example.com");
    }

    @Test
    public void testFetchStudentDetailsNotFound() throws Exception {
        when(studentService.fetchDetails(anyString())).thenThrow(
                new ResourceNotFoundException("User", "Email", "notfound@example.com")
        );

        mockMvc.perform(get("/api/fetch")
                        .param("email", "notfound@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with Email: notfound@example.com"));

        verify(studentService, times(1)).fetchDetails("notfound@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "mentor", "mentee"})
    public void testFetchRole(String role) throws Exception {
        UserDto userDto = new UserDto("test@example.com", "password", role);

        when(studentService.fetchDetailsByRole(anyString())).thenReturn(Collections.singletonList(userDto));

        mockMvc.perform(get("/api/role/{role}", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleName").value(role));

        verify(studentService, times(1)).fetchDetailsByRole(role);
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(studentService.deleteUser(anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/delete")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted Successfully"));

        verify(studentService, times(1)).deleteUser("test@example.com");
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        when(studentService.deleteUser(anyString())).thenReturn(false);

        mockMvc.perform(delete("/api/delete")
                        .param("email", "notfound@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unable To Delete"));

        verify(studentService, times(1)).deleteUser("notfound@example.com");
    }
}

