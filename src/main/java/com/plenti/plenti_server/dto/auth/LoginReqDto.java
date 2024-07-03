package com.plenti.plenti_server.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqDto {

  @NotNull
  @Size(min = 3, max = 50)
  private String email;

  @NotNull
  @Size(min = 8, max = 100)
  private String password;
}
