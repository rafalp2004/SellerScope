package com.course.leverxproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Column(name = "message")
    String message;
    @Column(name = "rate")
    short rate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved")
    private Boolean approved;

    public Comment(String message, short rate, User author, User seller, LocalDateTime createdAt, Boolean approved) {
        this.message = message;
        this.rate = rate;
        this.author = author;
        this.seller = seller;
        this.createdAt = createdAt;
        this.approved = approved;
    }
}
