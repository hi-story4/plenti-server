package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.auth.LoginReqDto;
import com.plenti.plenti_server.dto.auth.LoginResDto;
import com.plenti.plenti_server.dto.auth.RegisterReqDto;
import com.plenti.plenti_server.dto.auth.RegisterResDto;
import com.plenti.plenti_server.dto.auth.TokenDto;
import com.plenti.plenti_server.security.Auth;
import com.plenti.plenti_server.security.JwtFilter;
import com.plenti.plenti_server.security.TokenProvider;
import com.plenti.plenti_server.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(
    TokenProvider tokenProvider,
    AuthenticationManagerBuilder authenticationManagerBuilder,
    AuthService authService
  ) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResDto> login(
    @Valid @RequestBody LoginReqDto loginDto,
    HttpServletResponse response
  ) {
    return ResponseEntity.ok(authService.login(loginDto, response));
  }

  @PostMapping("/logout")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<SuccessDto> logout(
    @RequestHeader(JwtFilter.AUTHORIZATION_HEADER) String accessBearerToken
  ) {
    return ResponseEntity.ok(authService.logout(accessBearerToken));
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResDto> register(
    @Valid @RequestBody RegisterReqDto registerReqDto
  ) {
    return ResponseEntity.ok(authService.register(registerReqDto));
  }

  @GetMapping("/login/oauth2/code/{registrationId}")
  public ResponseEntity<LoginResDto> googleLogin(
    @RequestParam String code,
    @PathVariable String registrationId,
    HttpServletResponse response
  ) {
    return ResponseEntity.ok(
      authService.socialLogin(code, registrationId, response)
    );
  }

  @PatchMapping("/reissue")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<LoginResDto> reissue(
    @Valid @RequestBody TokenDto tokenDto,
    HttpServletResponse response
  ) {
    return ResponseEntity.ok(
      authService.reissue(tokenDto.getToken(), response)
    );
  }
}
