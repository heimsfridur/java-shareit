package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsNotUniqueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceMemoryImpl implements UserService {
    private Map<Integer, User> users = new HashMap<>();
    private int id = 1;
    private final UserMapper userMapper;


    @Override
    public UserDto add(UserDto userDto) {
        validateUserDtoEmail(userDto);
        User user =  userMapper.toUser(userDto);
        user.setId(id++);
        users.put(user.getId(),user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int id, UserDto newUserDto) {
        validateById(id);
        User oldUser = users.get(id);
        String newEmail = newUserDto.getEmail();
        String newName = newUserDto.getName();
        if (newEmail != null && !newEmail.equals(oldUser.getEmail())) {
            validateUserDtoEmail(newUserDto);
            users.get(id).setEmail(newEmail);
        }
        if (newName != null) {
            users.get(id).setName(newName);
        }
        return userMapper.toUserDto(users.get(id));
    }

    @Override
    public void deleteById(int id) {
        validateById(id);
        users.remove(id);
    }

    @Override
    public List<UserDto> getAll() {
        return users.values().stream().map(user -> userMapper.toUserDto(user)).toList();
    }

    @Override
    public UserDto getById(int id) {
        validateById(id);
        return userMapper.toUserDto(users.get(id));
    }

    private void validateUserDtoEmail(UserDto userDto) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsNotUniqueException(String.format("Failed to add user with email %s, email is not unique.", userDto.getEmail()));
        }
    }

    public void validateById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }
}
