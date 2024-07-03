package com.plenti.plenti_server.dto.comment;

import com.plenti.plenti_server.entity.Comment;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResDto {

  private Long commentId;

  private String content;

  private Long writerId;

  private String writerNm;

  private LocalDateTime createDt;

  private Boolean blindSt;

  public static CommentResDto from(Comment comment) {
    if (comment == null) return null;

    return CommentResDto
      .builder()
      .commentId(comment.getCommentId())
      .content(comment.getContent())
      .writerId(comment.getWriter().getMemberId())
      .writerNm(comment.getWriter().getUserNm())
      .createDt(comment.getCreatedAt())
      .blindSt(comment.isBlindSt())
      .build();
  }
}
