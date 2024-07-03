package com.plenti.plenti_server.dto.scrap;

import com.plenti.plenti_server.entity.Quiz;
import com.plenti.plenti_server.entity.Scrap;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class MyScrapListResDto {

  private String scrapNm;
  private Long scrapId;
  private LocalDateTime scrapDt;
  private String[] scrapQuizes;

  public static MyScrapListResDto from(Scrap scrap) {
    if (scrap == null) return null;

    List<String> coverImgs = new ArrayList<>();
    for (Quiz quiz : scrap.getScrapQuizes()) {
      coverImgs.add(quiz.getCoverImg());
    }

    return MyScrapListResDto
      .builder()
      .scrapNm(scrap.getScrapNm())
      .scrapId(scrap.getScrapId())
      .scrapDt(scrap.getCreatedAt())
      .scrapQuizes(coverImgs.toArray(new String[0]))
      .build();
  }
}
