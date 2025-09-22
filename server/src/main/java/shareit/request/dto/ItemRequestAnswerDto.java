package shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.request.RequestItemAnswer;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestAnswerDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    List<RequestItemAnswer> items;
}
