package com.plenti.plenti_server.dto.quiz;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizReqDto {

  @NotNull
  @NotEmpty
  private String quizNm;

  @NotNull
  private String content;

  private String coverImg;

  @NotNull
  private String category;

  @NotNull
  @NotEmpty
  private List<QuizItemDto> quizItems;
}
