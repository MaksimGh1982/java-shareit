package gateway.item;

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
public class ItemService extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemService(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAllByUser(Long userId) {
        log.info("Поиск всех вещей пользователя id = {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> create(@Valid ItemDto itemDto, Long userId) {
        log.info("Создать вещь = {}", itemDto);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto newItemDto, Long itemId, Long userId) {
        log.info("Обновить вещь id = {} пользователя = {}", itemId, userId);
        return post("/" + itemId, userId, newItemDto);
    }

    public ResponseEntity<Object> findItemById(long id) {
        log.info("Поиск вещи id = {}", id);
        return get("/" + id);
    }

    public ResponseEntity<Object> searchItem(String text) {
        log.info("Поиск вещи id = {}", text);
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(CommentDto newCommentDto, long itemId, long userId) {
        log.info("Добавление комментария к вещи id = {} пользователем = {}", itemId, userId);
        return post("/" + itemId + "/comment", userId, newCommentDto);
    }
}
