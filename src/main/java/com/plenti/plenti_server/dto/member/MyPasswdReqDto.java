package com.plenti.plenti_server.dto.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPasswdReqDto {

  @NotNull
  @Size(min = 8, max = 100)
  private String password;
}
