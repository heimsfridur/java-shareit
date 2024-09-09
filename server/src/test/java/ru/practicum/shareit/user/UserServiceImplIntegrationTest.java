package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).name("user").email("user@test.ru").build();
        user = userRepository.save(user);
    }

    @Test
    void addUser_ShouldAddUser() {
        UserDto newUserDto = UserDto.builder()
                .name("newUser")
                .email("newuser@test.ru")
                .build();

        UserDto savedUserDto = userService.add(newUserDto);

        assertThat(savedUserDto.getId()).isNotNull();
        assertThat(savedUserDto.getName()).isEqualTo("newUser");
        assertThat(savedUserDto.getEmail()).isEqualTo("newuser@test.ru");

        User savedUser = userRepository.findById(savedUserDto.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("newUser");
        assertThat(savedUser.getEmail()).isEqualTo("newuser@test.ru");
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        UserDto updatedUserDto = UserDto.builder()
                .name("updatedName")
                .email("updated@test.ru")
                .build();

        UserDto resultDto = userService.update(user.getId(), updatedUserDto);

        assertThat(resultDto.getName()).isEqualTo("updatedName");
        assertThat(resultDto.getEmail()).isEqualTo("updated@test.ru");

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("updatedName");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.ru");
    }

    @Test
    void deleteUserById_ShouldDeleteUserSuccessfully() {
        userService.deleteById(user.getId());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserDto userDto = userService.getById(user.getId());

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<UserDto> users = userService.getAll();

        assertEquals(1, users.size());
        assertThat(users.get(0).getName()).isEqualTo(user.getName());
        assertThat(users.get(0).getEmail()).isEqualTo(user.getEmail());
    }
}
