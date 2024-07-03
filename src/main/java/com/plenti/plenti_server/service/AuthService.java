package com.plenti.plenti_server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.auth.LoginReqDto;
import com.plenti.plenti_server.dto.auth.LoginResDto;
import com.plenti.plenti_server.dto.auth.RegisterReqDto;
import com.plenti.plenti_server.dto.auth.RegisterResDto;
import com.plenti.plenti_server.entity.Authority;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.DuplicateMemberException;
import com.plenti.plenti_server.exception.ForbiddenException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import com.plenti.plenti_server.repository.MemberRepository;
import com.plenti.plenti_server.security.JwtFilter;
import com.plenti.plenti_server.security.TokenProvider;
import com.plenti.plenti_server.util.RandomStringUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final Environment env;
  private final RestTemplate restTemplate = new RestTemplate();

  public AuthService(
    MemberRepository memberRepository,
    PasswordEncoder passwordEncoder,
    AuthenticationManagerBuilder authenticationManagerBuilder,
    TokenProvider tokenProvider,
    Environment env
  ) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.tokenProvider = tokenProvider;
    this.env = env;
  }

  @Transactional
  public LoginResDto login(
    LoginReqDto loginReqDto,
    HttpServletResponse response
  ) {
    if (
      memberRepository
        .findOneWithAuthoritiesByEmailAndBanSt(loginReqDto.getEmail(), true)
        .orElse(null) !=
      null
    ) {
      throw new ForbiddenException("사용이 금지된 유저입니다.");
    }

    try {
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginReqDto.getEmail(),
        loginReqDto.getPassword()
      );

      Authentication authentication = authenticationManagerBuilder
        .getObject()
        .authenticate(authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      String accessToken = tokenProvider.createAccessToken(authentication);
      String refreshToken = tokenProvider.issueRefreshToken(authentication);

      ResponseCookie cookie = ResponseCookie
        .from("refresh_token", refreshToken)
        .maxAge(60 * 30) // 쿠키 expiration 타임: 30분
        .httpOnly(true)
        .sameSite("Strict")
        .secure(true)
        .path("/")
        .build();
      response.addHeader("Set-Cookie", cookie.toString());

      return LoginResDto.from(
        memberRepository
          .findOneWithAuthoritiesByEmail(loginReqDto.getEmail())
          .orElse(null),
        accessToken,
        refreshToken
      );
    } catch (Exception e) {
      throw new BadRequestException("로그인 실패");
    }
  }

  @Transactional
  public SuccessDto logout(String accessBearerToken) {
    String accessToken = accessBearerToken.startsWith("Bearer ")
      ? accessBearerToken.substring(7)
      : accessBearerToken;
    Authentication authentication = tokenProvider.getAuthentication(
      accessToken
    );
    tokenProvider.issueRefreshToken(authentication);
    return new SuccessDto(200, "success!");
  }

  @Transactional
  public RegisterResDto register(RegisterReqDto registerReqDto) {
    if (
      memberRepository
        .findOneWithAuthoritiesByEmail(registerReqDto.getEmail())
        .orElse(null) !=
      null
    ) {
      throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
    }

    if (
      memberRepository
        .findOneWithAuthoritiesByUserNm(registerReqDto.getUserNm())
        .orElse(null) !=
      null
    ) {
      throw new DuplicateMemberException("중복되는 유저 이름입니다.");
    }

    Authority authority = Authority
      .builder()
      .authorityName(AuthRoleEnum.ROLE_USER.name())
      .build();

    Member member = Member
      .builder()
      .email(registerReqDto.getEmail())
      .password(passwordEncoder.encode(registerReqDto.getPassword()))
      .userNm(registerReqDto.getUserNm())
      .authorities(Collections.singleton(authority))
      .banSt(false)
      .build();

    return RegisterResDto.from(memberRepository.save(member));
  }

  @Transactional
  public LoginResDto socialLogin(
    String code,
    String registrationId,
    HttpServletResponse response
  ) {
    String accessToken = getAccessToken(code, registrationId);
    JsonNode userResourceNode = getUserResource(accessToken, registrationId);

    String id = userResourceNode.get("id").asText();
    String email = userResourceNode.get("email").asText();
    String userNm = registrationId + "_" + id;

    Member member = memberRepository
      .findOneWithAuthoritiesByEmail(email)
      .orElse(null);

    // 회원가입된 정보가 없다면 새로 생성
    if (member == null) {
      Authority authority = Authority
        .builder()
        .authorityName(AuthRoleEnum.ROLE_USER.name())
        .build();

      member =
        Member
          .builder()
          .email(email)
          .password(passwordEncoder.encode(RandomStringUtil.randomString(30))) //임의로 비밀번호 생성 -> 랜덤으로?
          .userNm(userNm)
          .provider(registrationId)
          .authorities(Collections.singleton(authority))
          .banSt(false)
          .build();

      memberRepository.save(member);
    }

    String jwtAccessToken = tokenProvider.createAccessTokenFromEmail(email);
    Authentication authentication = tokenProvider.getAuthentication(
      jwtAccessToken
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwtRefreshToken = tokenProvider.issueRefreshToken(authentication);

    ResponseCookie cookie = ResponseCookie
      .from("refresh_token", jwtRefreshToken)
      .maxAge(60 * 30) // 쿠키 expiration 타임: 30분
      .httpOnly(true)
      .sameSite("Strict")
      .secure(true)
      .path("/")
      .build();
    response.addHeader("Set-Cookie", cookie.toString());

    return LoginResDto.from(member, jwtAccessToken, jwtRefreshToken);
  }

  private String getAccessToken(
    String authorizationCode,
    String registrationId
  ) {
    String clientId = env.getProperty(
      "oauth2." + registrationId + ".client-id"
    );
    String clientSecret = env.getProperty(
      "oauth2." + registrationId + ".client-secret"
    );
    String redirectUri = env.getProperty(
      "oauth2." + registrationId + ".redirect-uri"
    );
    String tokenUri = env.getProperty(
      "oauth2." + registrationId + ".token-uri"
    );

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", authorizationCode);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity entity = new HttpEntity<>(params, headers);

    ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
      tokenUri,
      HttpMethod.POST,
      entity,
      JsonNode.class
    );
    JsonNode accessTokenNode = responseNode.getBody();
    return accessTokenNode.get("access_token").asText();
  }

  private JsonNode getUserResource(String accessToken, String registrationId) {
    String resourceUri = env.getProperty(
      "oauth2." + registrationId + ".resource-uri"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.set(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
    HttpEntity entity = new HttpEntity<>(headers);
    return restTemplate
      .exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class)
      .getBody();
  }

  @Transactional
  public LoginResDto reissue(
    String refreshToken,
    HttpServletResponse response
  ) {
    if (tokenProvider.validateToken(refreshToken)) {
      String newRefreshToken = tokenProvider.reissueRefreshToken(refreshToken);
      if (newRefreshToken != null) {
        Authentication authentication = tokenProvider.getAuthentication(
          newRefreshToken
        );
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        /// 헤더에 Access, Refresh 토큰 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(
          JwtFilter.AUTHORIZATION_HEADER,
          "Bearer " + newAccessToken
        );
        /// 컨텍스트에 넣기
        Authentication newAuthentication = tokenProvider.getAuthentication(
          newAccessToken
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        Member member = memberRepository
          .findOneWithAuthoritiesByEmail(newAuthentication.getName())
          .orElse(null);

        ResponseCookie cookie = ResponseCookie
          .from("refresh_token", newRefreshToken)
          .maxAge(60 * 30) // 쿠키 expiration 타임: 30분
          .httpOnly(true)
          .sameSite("Strict")
          .secure(true)
          .path("/")
          .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return LoginResDto.from(member, newAccessToken, newRefreshToken);
      }
      // else refresh 일치 X
      throw new UnauthorizedException("refreshToken이 일치하지 않습니다.");
    }
    // else logout
    throw new UnauthorizedException("refreshToken이 만료되었습니다.");
  }
}
