package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "scrap")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE scrap SET deleted_at = CURRENT_TIMESTAMP where scrap_id = ?"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scrap extends Timestamped {

  @Id
  @Column(name = "scrap_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long scrapId;

  @NotNull
  @Column(name = "scrap_nm", length = 100)
  private String scrapNm;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scrap_user_id")
  private Member scrapUser;

  @ManyToMany
  @JoinTable(
    name = "scrap_quiz",
    joinColumns = @JoinColumn(name = "scrap_id"),
    inverseJoinColumns = @JoinColumn(name = "quiz_id")
  )
  private List<Quiz> scrapQuizes;
}
