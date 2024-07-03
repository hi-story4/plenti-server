package com.plenti.plenti_server.dto.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoReqDto {
  
  @NotNull
  @Size(min = 3, max = 20)
  private String userNm;

}
