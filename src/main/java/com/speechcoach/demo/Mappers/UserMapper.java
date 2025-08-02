package com.speechcoach.demo.Mappers;

import com.speechcoach.demo.DTOs.UserResponseDto;
import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity mapFrom(CreateUserDto createUserDto){
        return new UserEntity(createUserDto.getName(), createUserDto.getUsername(), createUserDto.getPassword());
    }
    public UserResponseDto mapTo(UserEntity userEntity){
        return new UserResponseDto(userEntity.getId(), userEntity.getName(), userEntity.getUsername());
    }
}
