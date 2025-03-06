package com.course.leverxproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@Data
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name="title")
  String message;

  @ManyToOne
  @JoinColumn(name="user_id")
  private User author;

  @Column(name="created_at")
  private LocalDateTime createdAt;


 @Column(name="approved")
 private Boolean approved;

}
