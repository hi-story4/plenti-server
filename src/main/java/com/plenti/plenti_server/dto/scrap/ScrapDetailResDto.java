package com.plenti.plenti_server.dto.scrap;

import com.plenti.plenti_server.dto.quiz.QuizListResDto;
import com.plenti.plenti_server.entity.Scrap;
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
public class ScrapDetailResDto {

  private String scrapNm;
  private LocalDateTime scrapDt;
  private List<QuizListResDto> scrapQuiz;

  public static ScrapDetailResDto from(Scrap scrap) {
    if (scrap == null) return null;

    return ScrapDetailResDto
      .builder()
      .scrapNm(scrap.getScrapNm())
      .scrapDt(scrap.getCreatedAt())
      .scrapQuiz(
        scrap
          .getScrapQuizes()
          .stream()
          .map(quiz -> QuizListResDto.from(quiz))
          .collect(Collectors.toList())
      )
      .build();
  }
}
