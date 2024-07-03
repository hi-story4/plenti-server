package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Quiz;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
  List<Quiz> findAllByWriterOrderByCreatedAtDesc(Member writer);
  List<Quiz> findAll(Specification<Quiz> spec, Sort sort);
}
