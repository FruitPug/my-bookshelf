package com.example.MyBookshelf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@NamedEntityGraph(
        name = "Book.withReviewsAndUsers",
        attributeNodes = @NamedAttributeNode(value = "reviewEntities", subgraph = "reviews"),
        subgraphs = @NamedSubgraph(
                name = "reviews",
                attributeNodes = @NamedAttributeNode("user")
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private double rating;
    private int reviewCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviewEntities = new ArrayList<>();

}
