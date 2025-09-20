package gateway.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Email должен быть корректным")
    @NotBlank(message = "Email должен быть указан")
    private String email;
}
