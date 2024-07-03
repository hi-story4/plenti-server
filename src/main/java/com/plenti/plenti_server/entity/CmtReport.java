package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "cmt_report")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE cmt_report SET deleted_at = CURRENT_TIMESTAMP where cmt_report_id = ?"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CmtReport extends Timestamped {

  @Id
  @Column(name = "cmt_report_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cmtReportId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cmt_report_user")
  private Member cmtReportUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cmt_reported")
  private Comment cmtReported;
}
