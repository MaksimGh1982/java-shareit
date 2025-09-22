package gateway.user;

import gateway.BaseClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@Slf4j
@Validated
public class UserService extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserService(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );

    }

    public ResponseEntity<Object> findAll() {
        log.info("Список пользователей");
        return get("");
    }

    public ResponseEntity<Object> findUserById(long id) {
        log.info("Пользователь id = {}", id);
        return get("/" + id);
    }

    public ResponseEntity<Object> create(@Valid UserDto userDto) {
        log.info("Создать пользователя  {}", userDto.getName());
        return post("", userDto);
    }

    public ResponseEntity<Object> update(UserDto newUserDto, long userId) {
        return patch("/" + userId, newUserDto);
    }

    public void deleteUser(long userId) {
        log.info("Удалить пользователя id = {}", userId);
        delete("/" + userId);
    }
}
