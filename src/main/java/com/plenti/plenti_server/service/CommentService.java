package com.plenti.plenti_server.service;

import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.comment.CommentReqDto;
import com.plenti.plenti_server.entity.CmtReport;
import com.plenti.plenti_server.entity.Comment;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Quiz;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.ForbiddenException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import com.plenti.plenti_server.repository.CmtReportRepository;
import com.plenti.plenti_server.repository.CommentRepository;
import com.plenti.plenti_server.repository.MemberRepository;
import com.plenti.plenti_server.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final CmtReportRepository cmtReportRepository;
  private final QuizRepository quizRepository;

  public CommentService(
    MemberRepository memberRepository,
    CommentRepository commentRepository,
    CmtReportRepository cmtReportRepository,
    QuizRepository quizRepository
  ) {
    this.memberRepository = memberRepository;
    this.commentRepository = commentRepository;
    this.cmtReportRepository = cmtReportRepository;
    this.quizRepository = quizRepository;
  }

  @Transactional
  public SuccessDto createComment(
    CommentReqDto commentReqDto,
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

    Comment comment = Comment
      .builder()
      .commentQuiz(quiz)
      .content(commentReqDto.getContent())
      .writer(member)
      .blindSt(commentReqDto.isBlindSt())
      .build();

    commentRepository.save(comment);

    return new SuccessDto(200, "success!");
  }

  //Transactional annotaion을 이용하면 데이터 처리중 오류가 발생했을때 모든 작업을 원상태로 복구할 수 있다.

  @Transactional
  public SuccessDto deleteComment(Long commentId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    Comment comment = commentRepository
      .findById(commentId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 댓글입니다."));

    if (comment.getWriter().getMemberId() == member.getMemberId()) {
      commentRepository.delete(comment);
      return new SuccessDto(200, "success");
    }
    throw new UnauthorizedException("이 댓글에 대한 삭제 권한이 없습니다.");
  }

  @Transactional
  public SuccessDto reportComment(Long commentId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Comment comment = commentRepository
      .findById(commentId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 댓글입니다."));

    if (comment.getWriter().getMemberId() != member.getMemberId()) {
      CmtReport cmtReport = CmtReport
        .builder()
        .cmtReportUser(member)
        .cmtReported(comment)
        .build();
      cmtReportRepository.save(cmtReport);
      return new SuccessDto(200, "success");
    }
    throw new ForbiddenException("이용자의 댓글입니다.");
  }

  public SuccessDto modifyComment(
    CommentReqDto commentReqDto,
    Long commentId,
    String userEmail
  ) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Comment comment = commentRepository
      .findById(commentId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 댓글입니다."));

    if (comment.getWriter().getMemberId() != member.getMemberId()) {
      throw new ForbiddenException("수정 권한이 없는 댓글입니다");
    }

    comment.setContent(commentReqDto.getContent());
    comment.setBlindSt(commentReqDto.isBlindSt());
    commentRepository.save(comment);
    return new SuccessDto(200, "success");
  }
}
