package com.plenti.plenti_server.service;

import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.comment.CommentResDto;
import com.plenti.plenti_server.dto.member.MyInfoReqDto;
import com.plenti.plenti_server.dto.member.MyInfoResDto;
import com.plenti.plenti_server.dto.member.MyPasswdReqDto;
import com.plenti.plenti_server.dto.quiz.QuizListResDto;
import com.plenti.plenti_server.entity.CmtReport;
import com.plenti.plenti_server.entity.Heart;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.QzReport;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.DuplicateMemberException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import com.plenti.plenti_server.repository.CmtReportRepository;
import com.plenti.plenti_server.repository.HeartRepository;
import com.plenti.plenti_server.repository.MemberRepository;
import com.plenti.plenti_server.repository.QuizRepository;
import com.plenti.plenti_server.repository.QzReportRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyService {

  private final MemberRepository memberRepository;
  private final QuizRepository quizRepository;
  private final QzReportRepository qzReportRepository;
  private final HeartRepository heartRepository;
  private final CmtReportRepository cmtReportRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  public MyService(
    MemberRepository memberRepository,
    QuizRepository quizRepository,
    HeartRepository heartRepository,
    CmtReportRepository cmtReportRepository,
    QzReportRepository qzReportRepository,
    AuthenticationManagerBuilder authenticationManagerBuilder
  ) {
    this.memberRepository = memberRepository;
    this.quizRepository = quizRepository;
    this.qzReportRepository = qzReportRepository;
    this.heartRepository = heartRepository;
    this.cmtReportRepository = cmtReportRepository;
    this.authenticationManagerBuilder = authenticationManagerBuilder;
  }

  @Transactional
  public List<QuizListResDto> getMyQuizListMaked(String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    List<QuizListResDto> myQuizList = quizRepository
      .findAllByWriterOrderByCreatedAtDesc(member)
      .stream()
      .map(quiz -> QuizListResDto.from(quiz))
      .collect(Collectors.toList());

    return myQuizList;
  }

  @Transactional
  public List<QuizListResDto> getMyQuizListHearted(String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    List<Heart> heartList = heartRepository.findByHeartUser(member);

    List<QuizListResDto> myHeartList = heartList
      .stream()
      .map(quiz -> QuizListResDto.from(quiz.getHeartQuiz()))
      .collect(Collectors.toList());
    return myHeartList;
  }

  @Transactional
  public List<QuizListResDto> getMyQuizListReported(String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    List<QzReport> qzReportList = qzReportRepository.findByQzReportUser(member);

    List<QuizListResDto> myQzReportList = qzReportList
      .stream()
      .map(quiz -> QuizListResDto.from(quiz.getQzReported()))
      .collect(Collectors.toList());
    return myQzReportList;
  }

  @Transactional
  public List<CommentResDto> getMyCommentReported(String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    List<CmtReport> cmtReportList = cmtReportRepository.findByCmtReportUser(
      member
    );

    List<CommentResDto> myCmtReportList = cmtReportList
      .stream()
      .map(comment -> CommentResDto.from(comment.getCmtReported()))
      .collect(Collectors.toList());
    return myCmtReportList;
  }

  @Transactional
  public MyInfoResDto getMyInfo(String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    return MyInfoResDto.from(member);
  }

  @Transactional
  public SuccessDto modifyMyInfo(MyInfoReqDto myInfoReqDto, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    if (
      memberRepository
        .findOneWithAuthoritiesByUserNm(myInfoReqDto.getUserNm())
        .orElse(null) !=
      null
    ) {
      throw new DuplicateMemberException("중복되는 유저 이름입니다.");
    }
    member.setUserNm(myInfoReqDto.getUserNm());

    return new SuccessDto(200, "success!");
  }

  @Transactional
  public SuccessDto checkPasswd(
    MyPasswdReqDto myPasswdReqDto,
    String userEmail
  ) {
    memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    try {
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        userEmail,
        myPasswdReqDto.getPassword()
      );

      Authentication authentication = authenticationManagerBuilder
        .getObject()
        .authenticate(authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      return new SuccessDto(200, "success!");
    } catch (Exception e) {
      throw new BadRequestException("비밀번호 인증 실패");
    }
  }
}
