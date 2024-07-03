package com.plenti.plenti_server.dto.quiz;

import java.util.List;
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
public class QuizItemListResDto {

  private String quizNm;
  private List<QuizItemDto> quizItems;

  public static QuizItemListResDto from(QuizResDto quiz) {
    if (quiz == null) return null;

    return QuizItemListResDto
      .builder()
      .quizNm(quiz.getQuizNm())
      .quizItems(quiz.getQuizItems())
      .build();
  }
}
