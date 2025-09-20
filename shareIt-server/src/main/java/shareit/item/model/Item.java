package shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import shareit.user.User;

import java.util.Objects;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    //@ManyToOne
    //@JoinColumn(name = "request_id")
    //@Nullable
    @Column(name = "request_id", nullable = true)
    private /*ItemRequest*/long request;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id != null && id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
