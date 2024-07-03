package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "comment")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE comment SET deleted_at = CURRENT_TIMESTAMP where comment_id = ?"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends Timestamped {

  @Id
  @Column(name = "comment_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  @NotNull
  @Column(name = "content", length = 500)
  private String content;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "writer_id")
  private Member writer;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_quiz_id")
  private Quiz commentQuiz;

  @Builder.Default
  @Column(name = "blind_st", columnDefinition = "boolean default 0")
  private boolean blindSt = false;

  @Builder.Default
  @Column(name = "report_no", columnDefinition = "int default 0")
  private Integer reportNo = 0;

  @Builder.Default
  @Column(name = "ban_st", columnDefinition = "boolean default 0")
  private boolean banSt = false;

  @PrePersist
  public void prePersist() {
    // this.reportNo = this.reportNo == null ? 0 : this.reportNo;
  }
}
