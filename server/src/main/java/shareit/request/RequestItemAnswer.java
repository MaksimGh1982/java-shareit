package shareit.request;

public record RequestItemAnswer(
        Long itemId,
        String name,
        Long ownerId
) {
}
