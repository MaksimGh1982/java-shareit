import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import shareit.ShareItApp;
import shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {

        String strDate = "20.09.2025 21:30:55";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(strDate, formatter);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "мне нужен компьютер",
                9L,
                date
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("мне нужен компьютер");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(9);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025@09@20 21%30%55");
    }
}
