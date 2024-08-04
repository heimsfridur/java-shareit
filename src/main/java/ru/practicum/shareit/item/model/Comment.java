package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class Comment {
    private int id;
    @NotBlank
    private String text;
    @NotNull
    private Item item;
    @NotNull
    private User author;
    private LocalDateTime created;
}
