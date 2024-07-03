package com.plenti.plenti_server.dto.scrap;

import com.plenti.plenti_server.dto.quiz.QuizListResDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ScrapDetailReqDto {

  @NotNull
  @NotEmpty
  private String scrapNm;

  @NotNull
  @NotEmpty
  private List<QuizListResDto> scrapQuiz;
}
