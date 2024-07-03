package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  @EntityGraph(attributePaths = "authorities")
  Optional<Member> findOneByEmail(String Email);

  Optional<Member> findOneWithAuthoritiesByEmail(String email);

  Optional<Member> findOneWithAuthoritiesByUserNm(String userNm);

  Optional<Member> findOneWithAuthoritiesByEmailAndBanSt(
    String email,
    boolean banSt
  );
}
