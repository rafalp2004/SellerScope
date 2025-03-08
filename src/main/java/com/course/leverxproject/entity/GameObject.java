package com.course.leverxproject.entity;

import com.course.leverxproject.enums.GameEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="game_objects")
@NoArgsConstructor
public class GameObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="title")
    private String title;

    @Column(name="text")
    private String text;

    @Column(name="game")
    @Enumerated(EnumType.STRING)
    private GameEnum game;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;


    public GameObject(String title, String text, GameEnum game, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.text = text;
        this.game = game;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
