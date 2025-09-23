import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.bind.annotation.PostMapping;

import shareit.item.ItemController;
import shareit.item.ItemService;
import shareit.item.dto.CommentDto;
import shareit.item.dto.ItemDto;
import shareit.item.dto.ItemDtoWithBookComment;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private ItemDtoWithBookComment itemDtoWithBookComment;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemDto = new ItemDto(
                1L,
                "Computer",
                "Apple",
                true,
                1L,
                1L);

        itemDtoWithBookComment = new ItemDtoWithBookComment(1L,
                "Computer",
                "Apple",
                true,
                1L,
                1L,
                null,
                null,
                List.of("com1", "com2"));

        commentDto = new CommentDto(1L, "comment", 1L, "Ivan", LocalDateTime.now());

        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    void saveNewItem() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = invocationOnMock.getArgument(0, ItemDto.class);
                    itemDto.setOwner(invocationOnMock.getArgument(1, Long.class));
                    return itemDto;
                });

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.owner").value(99));
    }

    @Test
    void updateNewItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = invocationOnMock.getArgument(0, ItemDto.class);
                    itemDto.setId(invocationOnMock.getArgument(1, Long.class));
                    itemDto.setOwner(invocationOnMock.getArgument(2, Long.class));
                    return itemDto;
                });

        mvc.perform(patch("/items/5")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.owner").value(99));
    }

    @PostMapping("/{itemId}/comment")
    @Test
    void addComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    CommentDto commentDto = invocationOnMock.getArgument(0, CommentDto.class);
                    commentDto.setItemId(invocationOnMock.getArgument(1, Long.class));
                    return commentDto;
                });

        mvc.perform(post("/items/5/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.itemId").value(5))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem(any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=qweqwe")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].owner").value(itemDto.getOwner()));
    }

    @Test
    void findAllByUser() throws Exception {
        when(itemService.findAllByUser(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(itemDtoWithBookComment);
                });

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoWithBookComment.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoWithBookComment.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDtoWithBookComment.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDtoWithBookComment.getAvailable()))
                .andExpect(jsonPath("$[0].owner").value(itemDtoWithBookComment.getOwner()));
    }

    @Test
    void findItemById() throws Exception {
        when(itemService.findItemById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return itemDtoWithBookComment;
                });

        ResultActions resultActions = mvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoWithBookComment.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoWithBookComment.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoWithBookComment.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoWithBookComment.getAvailable()))
                .andExpect(jsonPath("$.owner").value(itemDtoWithBookComment.getOwner()));
    }


}
