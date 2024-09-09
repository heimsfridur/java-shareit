package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .id(1)
                .text("This is a comment.")
                .itemId(123)
                .authorName("Ivan")
                .created(LocalDateTime.of(2024, 8, 24, 15, 30))
                .build();
    }

    @Test
    void serializeCommentDto() throws Exception {
        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("This is a comment.");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(123);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-08-24T15:30:00");
    }


}
