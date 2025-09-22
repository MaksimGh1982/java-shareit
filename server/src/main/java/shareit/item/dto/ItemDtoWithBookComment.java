package shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.DateBooking;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBookComment {
    private Long id;
    @NotBlank(message = "Наименование должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть указано")
    private String description;
    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;
    private Long owner;
    private Long request;
    private DateBooking lastBooking;
    private DateBooking nextBooking;
    private List<String> comments;
}
