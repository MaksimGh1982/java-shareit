package gateway.item;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Наименование должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть указано")
    private String description;
    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;
    private Long owner;
    @Nullable
    private long requestId;

}
