package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE quiz SET deleted_at = CURRENT_TIMESTAMP where quiz_id = ?"
)
public class Quiz extends Timestamped {

  @Id
  @Column(name = "quiz_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long quizId;

  @NotNull
  @Column(name = "quiz_nm", length = 100)
  private String quizNm;

  @NotNull
  @Column(name = "content", length = 50)
  private String content;

  @Column(name = "cover_img")
  private String coverImg;

  @NotNull
  @Column(name = "category", length = 10)
  private String category;

  @Builder.Default
  @Column(name = "report_no", columnDefinition = "int default 0")
  private Integer reportNo = 0;

  @Builder.Default
  @Column(name = "ban_st", columnDefinition = "boolean default 0")
  private boolean banSt = false;

  @Builder.Default
  @Column(name = "view_cnt", columnDefinition = "int default 0")
  private Integer viewCnt = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "writer_id")
  private Member writer;

  @Builder.Default
  @ElementCollection
  private List<QuizItem> quizItems = new ArrayList<>();

  //자식 생명주기 관리를 위해
  @Builder.Default
  @OneToMany(
    mappedBy = "commentQuiz",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<Comment> comments = new ArrayList<Comment>();

  @Builder.Default
  @OneToMany(
    mappedBy = "heartQuiz",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<Heart> hearts = new ArrayList<Heart>();

  @PrePersist
  public void prePersist() {
    // this.reportNo = this.reportNo == null ? 0 : this.reportNo;
    // this.viewCnt = this.viewCnt == null ? 0 : this.viewCnt;
  }
}
