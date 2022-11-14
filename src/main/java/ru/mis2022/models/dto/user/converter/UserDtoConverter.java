package ru.mis2022.models.dto.user.converter;

import org.springframework.stereotype.Component;
import ru.mis2022.models.dto.user.UserDto;
import ru.mis2022.models.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDtoConverter {

    public List<UserDto> toListDto(List<User> users) {
        List<UserDto> usersDtoList = new ArrayList<>();
        users.forEach(user -> usersDtoList.add(toDto(user)));
        return usersDtoList;
    }

    public UserDto toDto(User entity) {
        String roleName = entity.getRole().getName();

        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .surName(entity.getSurname())
                .birthday(String.valueOf(entity.getBirthday()))
                .roleName(roleName)
                .build();
    }
}
