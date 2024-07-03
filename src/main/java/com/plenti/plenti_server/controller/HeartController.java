package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.heart.HeartResDto;
import com.plenti.plenti_server.security.Auth;
import com.plenti.plenti_server.service.HeartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/heart")
public class HeartController {

  private final HeartService heartService;

  public HeartController(HeartService heartService) {
    this.heartService = heartService;
  }

  @PostMapping("/{quizId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> createHeart(
    @AuthenticationPrincipal User user,
    @PathVariable Long quizId
  ) {
    return ResponseEntity.ok(
      heartService.createHeart(quizId, user.getUsername())
    );
  }

  @GetMapping("/{quizId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<HeartResDto> getHeart(
    @AuthenticationPrincipal User user,
    @PathVariable Long quizId
  ) {
    return ResponseEntity.ok(heartService.getHeart(quizId, user.getUsername()));
  }

  @DeleteMapping("/{quizId}")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> deleteQuiz(
    @PathVariable Long quizId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      heartService.deleteHeart(quizId, user.getUsername())
    );
  }
}
