package com.plenti.plenti_server.service;

import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.quiz.QuizItemListResDto;
import com.plenti.plenti_server.dto.quiz.QuizListResDto;
import com.plenti.plenti_server.dto.quiz.QuizReqDto;
import com.plenti.plenti_server.dto.quiz.QuizResDto;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Quiz;
import com.plenti.plenti_server.entity.QuizItem;
import com.plenti.plenti_server.entity.QzReport;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import com.plenti.plenti_server.repository.CommentRepository;
import com.plenti.plenti_server.repository.MemberRepository;
import com.plenti.plenti_server.repository.QuizRepository;
import com.plenti.plenti_server.repository.QzReportRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

  private final MemberRepository memberRepository;
  private final QuizRepository quizRepository;
  private final QzReportRepository qzReportRepository;

  public QuizService(
    MemberRepository memberRepository,
    QuizRepository quizRepository,
    CommentRepository commentRepository,
    QzReportRepository qzReportRepository
  ) {
    this.memberRepository = memberRepository;
    this.quizRepository = quizRepository;
    this.qzReportRepository = qzReportRepository;
  }

  @Transactional
  public QuizResDto getQuiz(Long quizId) {
    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다."));

    return QuizResDto.from(quiz);
  }

  public QuizItemListResDto getQuizItemList(Long quizId) {
    QuizResDto quiz = getQuiz(quizId);

    return QuizItemListResDto.from(quiz);
  }

  private Specification<Quiz> search(String search, String category) {
    return new Specification<Quiz>() {
      @Override
      public Predicate toPredicate(
        Root<Quiz> q,
        CriteriaQuery<?> query,
        CriteriaBuilder cb
      ) {
        query.distinct(true);

        // left outer join하여 Memeber 객체와 묶기
        Join<Quiz, Member> mem = q.join("writer", JoinType.LEFT);

        if (search.isEmpty()) {
          if (category.isEmpty()) {
            return cb.conjunction();
          }
          return (cb.like(q.get("category"), category));
        } else {
          if (category.isEmpty()) {
            return cb.or(
              cb.like(q.get("quizNm"), "%" + search + "%"),
              cb.like(q.get("content"), "%" + search + "%"),
              cb.like(mem.get("userNm"), "%" + search + "%")
            );
          }
          return cb.and(
            cb.like(q.get("category"), category),
            cb.or(
              cb.like(q.get("quiz_nm"), "%" + search + "%"),
              cb.like(q.get("content"), "%" + search + "%"),
              cb.like(mem.get("user_nm"), "%" + search + "%")
            )
          );
        }
      }
    };
  }

  @Transactional
  public List<QuizListResDto> getQuizList(
    String searchStr,
    String sortStr,
    String categoryStr
  ) {
    List<Sort.Order> sorts = new ArrayList<>();
    if (sortStr == "new") sorts.add(Sort.Order.desc("created_at")); else if (
      sortStr == "hot"
    ) sorts.add(Sort.Order.desc("view_cnt"));

    Specification<Quiz> spec = search(searchStr, categoryStr);
    List<Quiz> quizList = this.quizRepository.findAll(spec, Sort.by(sorts));

    List<QuizListResDto> quizListResDtos = quizList
      .stream()
      .map(quiz -> QuizListResDto.from(quiz))
      .collect(Collectors.toList());
    return quizListResDtos;
  }

  @Transactional
  public SuccessDto createQuiz(QuizReqDto quizReqDto, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    List<QuizItem> quizItems = quizReqDto
      .getQuizItems()
      .stream()
      .map(quizItem ->
        QuizItem
          .builder()
          .question(quizItem.getQuestion())
          .answer(quizItem.getAnswer())
          .build()
      )
      .collect(Collectors.toList());

    Quiz quiz = Quiz
      .builder()
      .quizNm(quizReqDto.getQuizNm())
      .content(quizReqDto.getContent())
      .coverImg(quizReqDto.getCoverImg())
      .category(quizReqDto.getCategory())
      .writer(member)
      .quizItems(quizItems)
      .build();

    quizRepository.save(quiz);

    return new SuccessDto(200, "success!");
  }

  @Transactional
  public SuccessDto modifyQuiz(
    QuizReqDto quizReqDto,
    Long quizId,
    String userEmail
  ) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() ->
        new BadRequestException("존재하지 않는 퀴즈 정보입니다.")
      );

    if (quiz.getWriter().getMemberId() != member.getMemberId()) {
      throw new UnauthorizedException("수정할 수 없는 퀴즈 정보입니다.");
    }

    List<QuizItem> quizItems = quizReqDto
      .getQuizItems()
      .stream()
      .map(quizItem ->
        QuizItem
          .builder()
          .question(quizItem.getQuestion())
          .answer(quizItem.getAnswer())
          .build()
      )
      .collect(Collectors.toList());

    quiz.setQuizNm(quizReqDto.getQuizNm());
    quiz.setContent(quizReqDto.getContent());
    quiz.setCategory(quizReqDto.getCategory());
    quiz.setCoverImg(quizReqDto.getCoverImg());
    quiz.setQuizItems(quizItems);
    quizRepository.save(quiz);
    return new SuccessDto(200, "success!");
  }

  @Transactional
  public SuccessDto deletexQuiz(Long quizId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다."));

    System.out.println(quiz.getWriter().getEmail());
    System.out.println(userEmail);

    if (quiz.getWriter().getMemberId() == member.getMemberId()) {
      quizRepository.delete(quiz);
      return new SuccessDto(200, "success!");
    }
    throw new UnauthorizedException("이 퀴즈에 대한 삭제 권한이 없습니다.");
  }

  @Transactional
  public SuccessDto reportQuiz(Long quizId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다."));

    QzReport qzReport = QzReport
      .builder()
      .qzReportUser(member)
      .qzReported(quiz)
      .build();

    qzReportRepository.save(qzReport);

    return new SuccessDto(200, "success!");
  }
}
