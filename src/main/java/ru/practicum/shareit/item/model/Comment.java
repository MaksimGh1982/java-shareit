package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments", schema = "public")
public class Comment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "text", nullable = false)
        private String text;
        @ManyToOne
        @JoinColumn(name = "item_id")
        private Item item;
        @ManyToOne
        @JoinColumn(name = "author_id")
        private User authorName;
        private LocalDateTime created;
}
