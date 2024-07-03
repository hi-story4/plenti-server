package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.scrap.MyScrapListResDto;
import com.plenti.plenti_server.dto.scrap.ScrapDetailResDto;
import com.plenti.plenti_server.dto.scrap.ScrapReqDto;
import com.plenti.plenti_server.dto.scrap.ScrapDelQuizDto;
import com.plenti.plenti_server.dto.scrap.ScrapAddQuizDto;
import com.plenti.plenti_server.security.Auth;
import com.plenti.plenti_server.service.ScrapService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrap")
public class ScrapController {

  private final ScrapService scrapService;

  public ScrapController(ScrapService scrapService) {
    this.scrapService = scrapService;
  }

  @Auth(role = AuthRoleEnum.ROLE_USER)
  @GetMapping("")
  public ResponseEntity<List<MyScrapListResDto>> getMyScrap(
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(scrapService.getMyScrapList(user.getUsername()));
  }

  @Auth(role = AuthRoleEnum.ROLE_USER)
  @DeleteMapping("/{scrapId}")
  public ResponseEntity<SuccessDto> deleteScrap(
    @PathVariable Long scrapId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      scrapService.deleteScrap(scrapId, user.getUsername())
    );
  }

  @PostMapping("")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> createScrap(
    @Valid @RequestBody ScrapReqDto scrapReqDto,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      scrapService.createScrap(scrapReqDto, user.getUsername())
    );
  }

  @Auth(role = AuthRoleEnum.ROLE_USER)
  @PatchMapping("/{scrapId}")
  public ResponseEntity<SuccessDto> modifyScrap(
    @Valid @RequestBody ScrapReqDto scrapReqDto,
    @PathVariable Long scrapId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      scrapService.modifyScrap(scrapReqDto, scrapId, user.getUsername())
    );
  }

  @Auth(role = AuthRoleEnum.ROLE_USER)
  @GetMapping("/{scrapId}")
  public ResponseEntity<ScrapDetailResDto> getScrapDetail(
    @PathVariable Long scrapId,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      scrapService.getScrapDetail(scrapId, user.getUsername())
    );
  }

  @Auth(role = AuthRoleEnum.ROLE_USER)
  @DeleteMapping("/delQuiz")
  public ResponseEntity<SuccessDto> scrapDelQuiz(
    @Valid @RequestBody ScrapDelQuizDto scrapDelQuizDto,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      scrapService.scrapDelQuiz(scrapDelQuizDto, user.getUsername())
    );
  }
  
  @Auth(role = AuthRoleEnum.ROLE_USER)
  @PostMapping("addQuiz")
  public ResponseEntity<SuccessDto> addQuiz(
    @Valid @RequestBody ScrapAddQuizDto scrapAddQuizDto,
    @AuthenticationPrincipal User user
  ) {
    return ResponseEntity.ok(
      scrapService.addQuiz(scrapAddQuizDto, user.getUsername())
    );
  }

}
