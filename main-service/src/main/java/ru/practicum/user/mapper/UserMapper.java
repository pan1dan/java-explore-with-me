package ru.practicum.user.mapper;

import ru.practicum.user.model.NewUserRequest;
import ru.practicum.user.model.UserDto;
import ru.practicum.user.model.UserShortDto;

public class UserMapper {

    public static UserDto fromNewUserRequestToUserDto(NewUserRequest newUserRequest) {
        return UserDto.builder()
                .id(null)
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
    }

    public static UserShortDto fromUserDtoToUserShortDto(UserDto userDto) {
        return UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();
    }

}
