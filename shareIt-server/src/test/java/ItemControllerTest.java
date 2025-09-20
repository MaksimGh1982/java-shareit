import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shareit.item.ItemController;
import shareit.item.ItemService;
import shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;

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
                1);
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
}
