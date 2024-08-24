package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1)
                .name("user name")
                .email("user.name@test.com")
                .build();
    }

    @Test
    void addUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.add(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).add(any(UserDto.class));
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        List<UserDto> users = List.of(userDto);
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));

        verify(userService, times(1)).getAll();
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getById(eq(1))).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).getById(eq(1));
    }

    @Test
    void deleteUserById_ShouldInvokeService() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(eq(1));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserDto updatedUserDto = UserDto.builder()
                .id(1)
                .name("new name")
                .email("new.name@test.com")
                .build();

        when(userService.update(eq(1), any(UserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedUserDto.getEmail()));

        verify(userService, times(1)).update(eq(1), any(UserDto.class));
    }

}
