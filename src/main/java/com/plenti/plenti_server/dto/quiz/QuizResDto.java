package com.plenti.plenti_server.dto.quiz;

import com.plenti.plenti_server.dto.comment.CommentResDto;
import com.plenti.plenti_server.entity.Quiz;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class QuizResDto {

  private Long quizId;
  private String quizNm;
  private String content;
  private Long writerId;
  private String writerNm;
  private String coverImg;
  private String category;
  private List<QuizItemDto> quizItems;
  private List<CommentResDto> comments;
  private Integer viewCnt;
  private Integer likeCnt;
  private Integer commentCnt;
  private LocalDateTime createDt;
  private LocalDateTime modiftDt;

  public static QuizResDto from(Quiz quiz) {
    if (quiz == null) return null;

    return QuizResDto
      .builder()
      .quizId(quiz.getQuizId())
      .quizNm(quiz.getQuizNm())
      .content(quiz.getContent())
      .writerId(quiz.getWriter().getMemberId())
      .writerNm(quiz.getWriter().getUserNm())
      .coverImg(quiz.getCoverImg())
      .category(quiz.getCategory())
      .quizItems(
        quiz
          .getQuizItems()
          .stream()
          .map(quizItem -> QuizItemDto.from(quizItem))
          .collect(Collectors.toList())
      )
      .comments(
        quiz
          .getComments()
          .stream()
          .map(comment -> CommentResDto.from(comment))
          .collect(Collectors.toList())
      )
      .viewCnt(quiz.getViewCnt())
      .likeCnt(quiz.getHearts().size())
      .commentCnt(quiz.getComments().size())
      .createDt(quiz.getCreatedAt())
      .modiftDt(quiz.getModifiedAt())
      .build();
  }
}
