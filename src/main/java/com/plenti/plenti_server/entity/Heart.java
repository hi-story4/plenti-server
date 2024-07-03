package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "heart")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE heart SET deleted_at = CURRENT_TIMESTAMP where heart_id = ?"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Heart extends Timestamped {

  @Id
  @Column(name = "heart_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long heartId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "heart_user_id")
  private Member heartUser;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "heart_quiz_id")
  private Quiz heartQuiz;
}
