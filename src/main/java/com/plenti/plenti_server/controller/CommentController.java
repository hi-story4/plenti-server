package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.comment.CommentReqDto;
import com.plenti.plenti_server.security.Auth;
import com.plenti.plenti_server.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping("/{quizId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> createComment(
    @Valid @RequestBody CommentReqDto commentReqDto,
    @PathVariable Long quizId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      commentService.createComment(commentReqDto, quizId, user.getUsername())
    );
  }

  @PatchMapping("/{commentId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> modifyComment(
    @Valid @RequestBody CommentReqDto commentReqDto,
    @PathVariable Long commentId,
    @AuthenticationPrincipal User user
  ) {
    System.out.println(commentReqDto.getContent());
    return ResponseEntity.ok(
      commentService.modifyComment(commentReqDto, commentId, user.getUsername())
    );
  }

  @DeleteMapping("/{commentId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> deleteComment(
    @PathVariable Long commentId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      commentService.deleteComment(commentId, user.getUsername())
    );
  }

  @PostMapping("/{commentId}/report")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> reportComment(
    @PathVariable Long commentId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      commentService.reportComment(commentId, user.getUsername())
    );
  }
}
