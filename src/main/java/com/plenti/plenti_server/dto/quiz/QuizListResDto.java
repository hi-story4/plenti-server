package com.plenti.plenti_server.dto.quiz;

import com.plenti.plenti_server.entity.Quiz;
import java.time.LocalDateTime;
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
public class QuizListResDto {

  private Long quizId;
  private String quizNm;
  private String content;
  private String writerNm;
  private String category;
  private LocalDateTime createDt;
  private Integer itemCnt;
  private Integer viewCnt;
  private Integer likeCnt;
  private Integer commentCnt;

  public static QuizListResDto from(Quiz quiz) {
    if (quiz == null) return null;

    return QuizListResDto
      .builder()
      .quizId(quiz.getQuizId())
      .quizNm(quiz.getQuizNm())
      .content(quiz.getContent())
      .writerNm(quiz.getWriter().getUserNm())
      .category(quiz.getCategory())
      .createDt(quiz.getCreatedAt())
      .itemCnt(quiz.getQuizItems().size())
      .viewCnt(quiz.getViewCnt())
      .likeCnt(quiz.getHearts().size())
      .commentCnt(quiz.getComments().size())
      .build();
  }
}
