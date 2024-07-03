package com.plenti.plenti_server.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterReqDto {

  @NotNull
  @Email
  @Size(min = 3, max = 50)
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotNull
  @Pattern(
    regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,16}",
    message = "비밀번호는 8~16자 영문 대 소문자, 숫자를 사용하세요."
  )
  @Size(min = 8, max = 100)
  private String password;

  @NotNull
  @Size(min = 3, max = 20)
  private String userNm;
}
