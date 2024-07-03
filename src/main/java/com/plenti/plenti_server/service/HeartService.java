package com.plenti.plenti_server.service;

import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.heart.HeartResDto;
import com.plenti.plenti_server.entity.Heart;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Quiz;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import com.plenti.plenti_server.repository.HeartRepository;
import com.plenti.plenti_server.repository.MemberRepository;
import com.plenti.plenti_server.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HeartService {

  private final MemberRepository memberRepository;
  private final HeartRepository heartRepository;
  private final QuizRepository quizRepository;

  public HeartService(
    MemberRepository memberRepository,
    HeartRepository heartRepository,
    QuizRepository quizRepository
  ) {
    this.memberRepository = memberRepository;
    this.heartRepository = heartRepository;
    this.quizRepository = quizRepository;
  }

  @Transactional
  public SuccessDto createHeart(Long quizId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다."));

    heartRepository
      .findByHeartUserAndHeartQuiz(member, quiz)
      .ifPresentOrElse(
        heart -> {},
        () -> {
          Heart heart = Heart
            .builder()
            .heartUser(member)
            .heartQuiz(quiz)
            .build();

          heartRepository.save(heart);
        }
      );

    return new SuccessDto(200, "success!");
  }

  @Transactional
  public HeartResDto getHeart(Long quizId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다."));

    Heart heart = heartRepository
      .findByHeartUserAndHeartQuiz(member, quiz)
      .orElse(null);
    HeartResDto heartResDto = new HeartResDto(quizId, heart != null);

    return heartResDto;
  }

  @Transactional
  public SuccessDto deleteHeart(Long quizId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다."));

    heartRepository
      .findByHeartUserAndHeartQuiz(member, quiz)
      .ifPresentOrElse(
        heart -> {
          heartRepository.delete(heart);
        },
        () -> {
          throw new BadRequestException("존재하지 않는 하트입니다.");
        }
      );
    return new SuccessDto(200, "success!");
  }
}
