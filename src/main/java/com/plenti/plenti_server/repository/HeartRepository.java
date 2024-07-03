package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.Heart;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Quiz;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {
  List<Heart> findByHeartUser(Member heartUser);
  Optional<Heart> findByHeartUserAndHeartQuiz(Member heartUser, Quiz heartQuiz);
}
