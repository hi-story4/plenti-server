package com.plenti.plenti_server.security;

import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  public CustomUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(final String email) {
    return memberRepository
      .findOneWithAuthoritiesByEmail(email)
      .map(user -> createUser(email, user))
      .orElseThrow(() ->
        new UsernameNotFoundException(
          email + " -> 데이터베이스에서 찾을 수 없습니다."
        )
      );
  }

  private org.springframework.security.core.userdetails.User createUser(
    String email,
    Member member
  ) {
    if (member.isBanSt()) {
      throw new RuntimeException(email + " -> 활성화되어 있지 않습니다.");
    }

    List<GrantedAuthority> grantedAuthorities = member
      .getAuthorities()
      .stream()
      .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName())
      )
      .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(
      member.getEmail(),
      member.getPassword(),
      grantedAuthorities
    );
  }
}
