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
import shareit.booking.BookStatus;
import shareit.booking.Booking;
import shareit.booking.BookingController;
import shareit.booking.BookingService;
import shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import shareit.item.model.Item;
import shareit.user.User;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String start = "20.09.2025 21:30:55";
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        String end = "21.09.2025 21:30:55";
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        bookingDto = new BookingDto(
                1L,
                startDate,
                endDate,
                1L,
                1L,
                BookStatus.WAITING);

        User user = new User(1L, "Ivan", "Ivan@mail.ru");

        Item item = new Item(1L, "Computer", "Apple", true, user, 1L);

        booking = new Booking(1L,
                startDate,
                endDate,
                item,
                user,
                BookStatus.WAITING
        );
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(booking);


        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value(BookStatus.WAITING.toString()));
    }

    @Test
    void aproved() throws Exception {
        when(bookingService.approved(anyLong(), anyString(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    booking.setStatus(BookStatus.APPROVED);
                    return booking;
                });

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value(BookStatus.APPROVED.toString()));
    }

    @Test
    void findBooking() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return booking;
                });

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value(BookStatus.WAITING.toString()));
    }

    @Test
    void getAllBookingByUser() throws Exception {
        when(bookingService.getAllBookingByUser(anyLong(), any()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(booking);
                });

        mvc.perform(get("/bookings?state=ALL")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].status").value(BookStatus.WAITING.toString()));
    }

    @Test
    void getAllBookingByItemsUser() throws Exception {
        when(bookingService.getAllBookingByItemsUser(anyLong(), any()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(booking);
                });

        mvc.perform(get("/bookings/owner?state=ALL")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].status").value(BookStatus.WAITING.toString()));
    }


}

