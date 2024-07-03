package com.plenti.plenti_server.dto.quiz;

import com.plenti.plenti_server.entity.QuizItem;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizItemDto {

  @NotNull
  @NotEmpty
  private String question;

  @NotNull
  @NotEmpty
  private String answer;

  public static QuizItemDto from(QuizItem quizItem) {
    if (quizItem == null) return null;

    return QuizItemDto
      .builder()
      .question(quizItem.getQuestion())
      .answer(quizItem.getAnswer())
      .build();
  }
}
