package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).name("user").email("user@test.ru").build();
        item1 = Item.builder().id(1).name("item1").description("item_descr1").available(true).owner(user).build();
        item2 = Item.builder().id(2).name("item2").description("item_descr2").available(true).owner(user).build();

        user = userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void getAllUserItems_ShouldReturnAllItemsForUser() {
        List<ItemDto> userItems = itemService.getAll(user.getId());

        assertThat(userItems).hasSize(2);
        assertThat(userItems.get(0).getName()).isEqualTo(item1.getName());
        assertThat(userItems.get(1).getName()).isEqualTo(item2.getName());
    }

    @Test
    void updateItem_ShouldUpdateItem() {
        ItemDto updatedItemDto = ItemDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .available(false)
                .build();

        ItemDto resultDto = itemService.update(item1.getId(), user.getId(), updatedItemDto);

        assertThat(resultDto.getName()).isEqualTo(updatedItemDto.getName());
        assertThat(resultDto.getDescription()).isEqualTo(updatedItemDto.getDescription());
        assertThat(resultDto.getAvailable()).isFalse();

        Item updatedItem = itemRepository.findById(item1.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("updatedName");
        assertThat(updatedItem.getDescription()).isEqualTo("updatedDescription");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void getItemById_ShouldReturnItem() {
        ItemDto itemDto = itemService.getById(item1.getId(), user.getId());

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(item1.getId());
        assertThat(itemDto.getName()).isEqualTo(item1.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item1.getDescription());
    }

    @Test
    void findItems_ShouldReturnItemsContainingText() {
        List<ItemDto> foundItems = itemService.find("item");

        assertThat(foundItems).hasSize(2);

        foundItems.forEach(itemDto -> {
            assertThat(itemDto.getName()).containsIgnoringCase("item");
        });
    }

    @Test
    void findItems_ShouldReturnEmptyListWhenNoMatches() {
        List<ItemDto> foundItems = itemService.find("jfksdjflks");

        assertThat(foundItems).isEmpty();
    }

    @Test
    void addComment_ShouldAddComment() {
        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        bookingRepository.save(booking);

        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        CommentDtoExport savedCommentDto = itemService.addComment(item1.getId(), user.getId(), commentDto);

        assertThat(savedCommentDto.getId()).isNotNull();
        assertThat(savedCommentDto.getText()).isEqualTo("Great item!");

        Comment savedComment = commentRepository.findById(savedCommentDto.getId()).orElseThrow();
        assertThat(savedComment.getText()).isEqualTo("Great item!");
        assertThat(savedComment.getItem().getId()).isEqualTo(item1.getId());
        assertThat(savedComment.getAuthor().getId()).isEqualTo(user.getId());
    }

}
