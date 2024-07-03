package com.plenti.plenti_server.dto.auth;

import com.plenti.plenti_server.entity.Member;
import java.util.List;
import java.util.stream.Collectors;
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
public class LoginResDto {

  private String accessToken;

  private String refreshToken;

  private String email;

  private String userNm;

  private boolean banSt;

  private List<String> userRoles;

  public static LoginResDto from(
    Member member,
    String accessToken,
    String refreshToken
  ) {
    if (member == null) return null;

    return LoginResDto
      .builder()
      .email(member.getEmail())
      .userNm(member.getUserNm())
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .banSt(member.isBanSt())
      .userRoles(
        member
          .getAuthorities()
          .stream()
          .map(authority -> authority.getAuthorityName().toString())
          .collect(Collectors.toList())
      )
      .build();
  }
}
