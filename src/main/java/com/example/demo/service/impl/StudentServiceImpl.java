package com.example.demo.service.impl;


import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IStudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements IStudentService {
    private UserRepository userRepository;


    @Override
    public void createUser(UserDto userDto) {
        String email = userDto.getEmail();
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isPresent()) {
            throw new CustomerAlreadyExistsException("User already exist for given mail" + email);

        }
        User user=UserMapper.mapToUser(userDto,new User());
        userRepository.save(user);
    }


    @Override
    public UserDto fetchDetails(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "Email", email)
        );


        UserDto userDto = UserMapper.mapToUserDto(user, new UserDto());

        return userDto;
    }

    @Override
    public boolean deleteUser(String email) {
        boolean isDeleted = false;
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "Email", email)
        );
        if (user != null) {
            userRepository.delete(user);
            isDeleted = true;
        }
        return isDeleted;
    }

    @Override
    public List<UserDto> fetchDetailsByRole(String roleName) {
        List<User> users= userRepository.findAllByRoleName(roleName);
       return users.stream().map(user -> UserMapper.mapToUserDto(user,new UserDto())).collect(Collectors.toList());
    }
}

