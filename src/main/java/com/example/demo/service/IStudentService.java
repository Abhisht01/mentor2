package com.example.demo.service;

import com.example.demo.dto.UserDto;

import java.util.List;

public interface IStudentService {
    void createUser(UserDto userDto);
    UserDto fetchDetails(String email);
    List<UserDto> fetchDetailsByRole(String roleName);
    boolean deleteUser(String email);
}
