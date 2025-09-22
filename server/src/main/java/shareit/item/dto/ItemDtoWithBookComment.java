package shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.booking.DateBooking;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBookComment {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
    private DateBooking lastBooking;
    private DateBooking nextBooking;
    private List<String> comments;
}
