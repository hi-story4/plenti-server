package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.comment.CommentResDto;
import com.plenti.plenti_server.dto.member.MyInfoReqDto;
import com.plenti.plenti_server.dto.member.MyInfoResDto;
import com.plenti.plenti_server.dto.member.MyPasswdReqDto;
import com.plenti.plenti_server.dto.quiz.QuizListResDto;
import com.plenti.plenti_server.security.Auth;
import com.plenti.plenti_server.service.MyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
public class MyController {

  private final MyService myService;

  public MyController(MyService myService) {
    this.myService = myService;
  }

  @GetMapping("/quiz/make")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<List<QuizListResDto>> getMyQuizListMaked(
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(myService.getMyQuizListMaked(user.getUsername()));
  }

  @Auth(role = AuthRoleEnum.ROLE_USER)
  @GetMapping("/quiz/like")
  public ResponseEntity<List<QuizListResDto>> likeQuiz(
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      myService.getMyQuizListHearted(user.getUsername())
    );
  }

  @GetMapping("/comment/report")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<List<CommentResDto>> getMyCommentReported(
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      myService.getMyCommentReported(user.getUsername())
    );
  }

  @GetMapping("/quiz/report")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<List<QuizListResDto>> reportQuiz(
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      myService.getMyQuizListReported(user.getUsername())
    );
  }

  @GetMapping("/member")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<MyInfoResDto> getMyInfo(
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(myService.getMyInfo(user.getUsername()));
  }

  @PatchMapping("/member")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> modifyMyInfo(
    @Valid @RequestBody MyInfoReqDto myInfoReqDto,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      myService.modifyMyInfo(myInfoReqDto, user.getUsername())
    );
  }

  @GetMapping("/passwd")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> checkPasswd(
    @Valid @RequestBody MyPasswdReqDto myPasswdReqDto,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      myService.checkPasswd(myPasswdReqDto, user.getUsername())
    );
  }
}
