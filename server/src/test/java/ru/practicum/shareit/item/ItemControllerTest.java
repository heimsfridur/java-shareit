package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private CommentDtoExport commentDtoExport;

    @BeforeEach
    void setup() {
        itemDto = ItemDto.builder()
                .id(1)
                .name("Item")
                .description("Item Description")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1)
                .text("Nice item!")
                .build();

        commentDtoExport = CommentDtoExport.builder()
                .id(1)
                .text("Nice item!")
                .authorName("Ivan")
                .build();
    }

    @Test
    void addItem_ShouldReturnItemDto() throws Exception {
        when(itemService.add(any(ItemDto.class), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItemDto() throws Exception {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(1)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemService.update(eq(1), eq(1), any(ItemDto.class)))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(updatedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(updatedItemDto.getAvailable()));
    }

    @Test
    void getItemById_ShouldReturnItemDto() throws Exception {
        when(itemService.getById(eq(1), eq(1)))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getAllItems_ShouldReturnListOfItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemService.getAll(eq(1)))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void findItems_ShouldReturnListOfItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemService.find(anyString()))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Item")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void addComment_ShouldReturnCommentDtoExport() throws Exception {
        when(itemService.addComment(eq(1), eq(1), any(CommentDto.class)))
                .thenReturn(commentDtoExport);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDtoExport.getId()))
                .andExpect(jsonPath("$.text").value(commentDtoExport.getText()));
    }
}
