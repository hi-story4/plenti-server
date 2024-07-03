package com.plenti.plenti_server.dto.heart;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class HeartResDto {

  private long quizId;
  private Boolean heart;

  public HeartResDto(long quizId, Boolean heart) {
    this.quizId = quizId;
    this.heart = heart;
  }
}
