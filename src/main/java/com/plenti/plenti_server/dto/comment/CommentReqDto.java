package com.plenti.plenti_server.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentReqDto {


  @NotNull
  private String content;

  private boolean blindSt;

}
