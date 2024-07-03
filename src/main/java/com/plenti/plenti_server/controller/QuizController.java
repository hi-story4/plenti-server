package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.quiz.QuizItemListResDto;
import com.plenti.plenti_server.dto.quiz.QuizListResDto;
import com.plenti.plenti_server.dto.quiz.QuizReqDto;
import com.plenti.plenti_server.dto.quiz.QuizResDto;
import com.plenti.plenti_server.security.Auth;
import com.plenti.plenti_server.service.QuizService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class QuizController {

  private final QuizService quizService;

  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }

  @GetMapping("/{quizId}")
  public ResponseEntity<QuizResDto> getQuiz(@PathVariable Long quizId) {
    return ResponseEntity.ok(quizService.getQuiz(quizId));
  }

  @GetMapping
  public ResponseEntity<List<QuizListResDto>> getQuizList(
    @RequestParam(value = "search", defaultValue = "") String searchString,
    @RequestParam(value = "sort", defaultValue = "new") String sortString,
    @RequestParam(value = "category", defaultValue = "") String categoryString
  ) {
    return ResponseEntity.ok(
      quizService.getQuizList(searchString, sortString, categoryString)
    );
  }

  @PostMapping
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> createQuiz(
    @Valid @RequestBody QuizReqDto quizReqDto,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      quizService.createQuiz(quizReqDto, user.getUsername())
    );
  }

  @PatchMapping("/{quizId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> modifyQuiz(
    @Valid @RequestBody QuizReqDto quizReqDto,
    @PathVariable Long quizId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      quizService.modifyQuiz(quizReqDto, quizId, user.getUsername())
    );
  }

  @DeleteMapping("/{quizId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> deleteQuiz(
    @PathVariable Long quizId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      quizService.deleteQuiz(quizId, user.getUsername())
    );
  }

  @PostMapping("/{quizId}/report")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> reportQuiz(
    @PathVariable Long quizId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      quizService.reportQuiz(quizId, user.getUsername())
    );
  }

  @GetMapping("/play/{quizId}")
  public ResponseEntity<QuizItemListResDto> playQuiz(
    @PathVariable Long quizId
  ) {
    return ResponseEntity.ok(quizService.getQuizItemList(quizId));
  }
}
