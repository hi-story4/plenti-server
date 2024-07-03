package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "qz_report")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE qz_report SET deleted_at = CURRENT_TIMESTAMP where qz_report_id = ?"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QzReport extends Timestamped {

  @Id
  @Column(name = "qz_report_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long qzReportId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "qz_report_user")
  private Member qzReportUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "qz_reported")
  private Quiz qzReported;
}
