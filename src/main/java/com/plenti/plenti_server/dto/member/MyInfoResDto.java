package com.plenti.plenti_server.dto.member;

import com.plenti.plenti_server.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoResDto {

  
  private String userNm;
  private String email;
  private Integer reportNo;
  private Boolean banSt;

  public static MyInfoResDto from(Member member) {
    if (member == null) return null;

    return MyInfoResDto
      .builder()
      .userNm(member.getUserNm())
      .email(member.getEmail())
      .reportNo(member.getReportNo())
      .banSt(member.isBanSt())
      .build();
  }
}
