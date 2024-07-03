package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.Comment;
import com.plenti.plenti_server.entity.Quiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByCommentQuiz(Quiz quiz);
}
