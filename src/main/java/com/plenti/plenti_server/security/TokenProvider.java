package com.plenti.plenti_server.security;

import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider implements InitializingBean {

  private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
  private static final String AUTHORITIES_KEY = "auth";
  private final String secret;
  private final long tokenValidityInMilliseconds;
  private final long refreshTokenValidityInMilliseconds;
  private final MemberRepository memberRepository;
  private Key key;

  public TokenProvider(
    @Value("${jwt.secret}") String secret,
    @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
    @Value(
      "${jwt.refresh-token-validity-in-seconds}"
    ) long refreshTokenValidityInSeconds,
    MemberRepository memberRepository
  ) {
    this.secret = secret;
    this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    this.refreshTokenValidityInMilliseconds =
      refreshTokenValidityInSeconds * 1000;
    this.memberRepository = memberRepository;
  }

  @Override
  public void afterPropertiesSet() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  @Transactional
  public String getEmailFromToken(String token) {
    final String payloadJWT = token.split("\\.")[1];
    Base64.Decoder decoder = Base64.getUrlDecoder();

    final String payload = new String(decoder.decode(payloadJWT));
    BasicJsonParser jsonParser = new BasicJsonParser();
    Map<String, Object> jsonArray = jsonParser.parseMap(payload);
    return jsonArray.get("sub").toString();
  }

  @Transactional
  public String createAccessToken(Authentication authentication) {
    String authorities = authentication
      .getAuthorities()
      .stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity = new Date(now + this.tokenValidityInMilliseconds);

    return Jwts
      .builder()
      .setSubject(authentication.getName())
      .claim(AUTHORITIES_KEY, authorities)
      .signWith(key, SignatureAlgorithm.HS512)
      .setExpiration(validity)
      .compact();
  }

  @Transactional
  public String createRefreshToken(Authentication authentication) {
    String authorities = authentication
      .getAuthorities()
      .stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);
    return Jwts
      .builder()
      .setSubject(authentication.getName())
      .claim(AUTHORITIES_KEY, authorities)
      .signWith(key, SignatureAlgorithm.HS512)
      .setExpiration(validity)
      .compact();
  }

  @Transactional
  public String reissueRefreshToken(String refreshToken)
    throws RuntimeException {
    // refresh token을 디비의 그것과 비교해보기
    Authentication authentication = getAuthentication(refreshToken);
    Member member = memberRepository
      .findOneByEmail(authentication.getName())
      .orElseThrow(() ->
        new BadRequestException(
          "email : " + authentication.getName() + " was not found"
        )
      );

    if (member.getRefreshToken().equals(refreshToken)) {
      // 새로운 refresh token 생성
      String newRefreshToken = createRefreshToken(authentication);

      member.setRefreshToken(newRefreshToken);
      memberRepository.save(member);

      return newRefreshToken;
    } else {
      logger.info("refresh 토큰이 일치하지 않습니다. ");
      return null;
    }
  }

  @Transactional
  public String issueRefreshToken(Authentication authentication) {
    String newRefreshToken = createRefreshToken(authentication);

    Member member = memberRepository
      .findOneByEmail(authentication.getName())
      .orElse(null);

    if (member != null) {
      member.setRefreshToken(newRefreshToken);
      memberRepository.save(member);
      logger.info("issueRefreshToken method | change token ");
      return newRefreshToken;
    }

    throw new BadRequestException(
      "email : " + authentication.getName() + " was not found"
    );
  }

  @Transactional
  public String createAccessTokenFromEmail(String email) {
    long now = (new Date()).getTime();
    Date validity = new Date(now + this.tokenValidityInMilliseconds);

    Member member = memberRepository
      .findOneWithAuthoritiesByEmail(email)
      .orElse(null);

    String authorities = member
      .getAuthorities()
      .stream()
      .map(authority -> authority.getAuthorityName())
      .collect(Collectors.joining(","));

    return Jwts
      .builder()
      .setSubject(email)
      .claim(AUTHORITIES_KEY, authorities)
      .setIssuedAt(new Date())
      .setExpiration(validity)
      .signWith(key, SignatureAlgorithm.HS512)
      .compact();
  }

  @Transactional
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts
      .parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();

    Collection<? extends GrantedAuthority> authorities = Arrays
      .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());

    User principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(
      principal,
      token,
      authorities
    );
  }

  @Transactional
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (
      io.jsonwebtoken.security.SecurityException | MalformedJwtException e
    ) {
      logger.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      logger.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      logger.info("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      logger.info("JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }
}
