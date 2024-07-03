package com.plenti.plenti_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(
  sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP where member_id = ?"
)
public class Member extends Timestamped {

  @Id
  @Column(name = "member_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @NotNull
  @Column(name = "user_nm", length = 20, unique = true)
  private String userNm;

  @NotNull
  @Column(name = "email", length = 50, unique = true)
  private String email;

  @NotNull
  @Column(name = "password", length = 100)
  private String password;

  @Column(name = "provider")
  private String provider;

  @Builder.Default
  @Column(name = "report_no", columnDefinition = "int default 0")
  private Integer reportNo = 0;

  @Builder.Default
  @Column(name = "ban_st", columnDefinition = "boolean default 0")
  private boolean banSt = false;

  @Column(name = "refresh_token")
  private String refreshToken;

  @ManyToMany
  @JoinTable(
    name = "member_authority",
    joinColumns = {
      @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
    },
    inverseJoinColumns = {
      @JoinColumn(
        name = "authority_name",
        referencedColumnName = "authority_name"
      ),
    }
  )
  private Set<Authority> authorities;

  @PrePersist
  public void prePersist() {
    // this.reportNo = this.reportNo == null ? 0 : this.reportNo;
  }
}
