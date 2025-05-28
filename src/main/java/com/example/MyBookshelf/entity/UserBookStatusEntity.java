package com.example.MyBookshelf.entity;

import com.example.MyBookshelf.enums.ReadingStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_book_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;
}
