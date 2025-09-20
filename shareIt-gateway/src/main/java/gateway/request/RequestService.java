package gateway.request;

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
public class RequestService extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestService(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(@Valid ItemRequestDto itemRequestDto, Long userId) {
        log.info("Создать запрос на вещь");
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequestByUser(Long userId) {
        log.info("Просмотр всех запросов пользователя = {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        log.info("Просмотреть все запросы");
        return get("/all", userId);
    }

    public ResponseEntity<Object> getRequestById(long requestId) {
        log.info("Просмотр запроса id = {}", requestId);
        return get("/" + requestId);
    }
}
