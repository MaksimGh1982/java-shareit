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
import shareit.request.ItemRequestController;
import shareit.request.RequestService;
import shareit.request.dto.ItemRequestAnswerDto;
import shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private RequestService requestService;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestAnswerDto itemRequestAnswerDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemRequestDto = new ItemRequestDto(
                1L,
                "Computer",
                1L,
                null
        );

        itemRequestAnswerDto = new ItemRequestAnswerDto(
                1L,
                "Computer",
                1L,
                null,
                null
        );
    }

    @Test
    void saveNewItemRequest() throws Exception {
        when(requestService.create(any(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto itemRequestDto = invocationOnMock.getArgument(0, ItemRequestDto.class);
                    itemRequestDto.setRequestorId(invocationOnMock.getArgument(1, Long.class));
                    return itemRequestDto;
                });

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorId").value(99));

    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getRequestById(anyLong()))
                .thenReturn(itemRequestAnswerDto);

        mvc.perform(get("/requests/5")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()));
    }
}
