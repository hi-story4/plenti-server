package com.plenti.plenti_server.dto.auth;

import com.plenti.plenti_server.entity.Member;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResDto {

  private String email;

  private String userNm;

  private Set<AuthorityDto> authorityDtoSet;

  public static RegisterResDto from(Member member) {
    if (member == null) return null;

    return RegisterResDto
      .builder()
      .email(member.getEmail())
      .userNm(member.getUserNm())
      .authorityDtoSet(
        member
          .getAuthorities()
          .stream()
          .map(authority ->
            AuthorityDto
              .builder()
              .authorityName(authority.getAuthorityName())
              .build()
          )
          .collect(Collectors.toSet())
      )
      .build();
  }
}
