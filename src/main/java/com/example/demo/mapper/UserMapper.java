package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;

public class UserMapper {
    public static User mapToUser(UserDto userDto,User user){
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRoleName(userDto.getRoleName());
        return user;
    }
    public static UserDto mapToUserDto(User user,UserDto userDto){
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setRoleName(user.getRoleName());
        return userDto;
    }

}
