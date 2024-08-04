package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsNotUniqueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto add(UserDto userDto) {
        validateUserDtoEmail(userDto);
        User user = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int id, UserDto newUserDto) {
        validateById(id);
        User oldUser = userRepository.findById(id).get();

        String newEmail = newUserDto.getEmail();
        if (newEmail != null && !newEmail.equals(oldUser.getEmail())) {
            validateUserDtoEmail(newUserDto);
            oldUser.setEmail(newEmail);
        }

        String newName = newUserDto.getName();
        if (newName != null) {
            oldUser.setName(newName);
        }

        User user = userRepository.save(oldUser);
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteById(int id) {
        validateById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(user -> userMapper.toUserDto(user)).toList();
    }

    @Override
    public UserDto getById(int id) {
        validateById(id);
        return userMapper.toUserDto(userRepository.findById(id).get());
    }

    private void validateUserDtoEmail(UserDto userDto) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsNotUniqueException(String.format("Failed to add user with email %s, email is not unique.", userDto.getEmail()));
        }
    }

    public void validateById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }
}
