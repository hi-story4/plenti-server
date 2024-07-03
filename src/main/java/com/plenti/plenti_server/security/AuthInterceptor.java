package com.plenti.plenti_server.security;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.exception.ForbiddenException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final TokenProvider tokenProvider;

  @Override
  public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  ) throws Exception {
    // 1. handler 종류 확인
    // 우리가 관심 있는 것은 Controller에 있는 메서드이므로 HandlerMethod 타입인지 체크
    if (handler instanceof HandlerMethod == false) {
      // return true이면  Controller에 있는 메서드가 아니므로, 그대로 컨트롤러로 진행
      return true;
    }

    // 2.형 변환
    HandlerMethod handlerMethod = (HandlerMethod) handler;

    // 3. @Auth 받아오기
    Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

    // 4. method에 @Auth가 없는 경우, 즉 인증이 필요 없는 요청
    if (auth == null) {
      return true;
    }

    // 5. @Auth가 있는 경우이므로, 토큰이 있는지 체크
    String bearerToken = request.getHeader(JwtFilter.AUTHORIZATION_HEADER);

    String accessToken = StringUtils.hasText(bearerToken) &&
      bearerToken.startsWith("Bearer ")
      ? bearerToken.substring(7)
      : null;

    if (accessToken == null) {
      throw new UnauthorizedException("로그인이 필요한 작업입니다.");
    }

    if (
      StringUtils.hasText(accessToken) &&
      tokenProvider.validateToken(accessToken)
    ) {
      Authentication authentication = tokenProvider.getAuthentication(
        accessToken
      );

      List<String> roleLists = authentication
        .getAuthorities()
        .stream()
        .map(authority -> authority.getAuthority().toString())
        .collect(Collectors.toList());

      if (auth.role().equals(AuthRoleEnum.ROLE_ADMIN)) {
        for (String roleList : roleLists) {
          if (roleList.equals(AuthRoleEnum.ROLE_ADMIN.name())) {
            return true;
          }
        }
        throw new ForbiddenException("관리자 권한이 필요한 작업입니다.");
      }
      return true;
    }

    throw new UnauthorizedException("유효하지 않은 토큰입니다.");
  }
}
