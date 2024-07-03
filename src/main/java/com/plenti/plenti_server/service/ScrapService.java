package com.plenti.plenti_server.service;

import com.plenti.plenti_server.dto.SuccessDto;
import com.plenti.plenti_server.dto.scrap.MyScrapListResDto;
import com.plenti.plenti_server.dto.scrap.ScrapDetailResDto;
import com.plenti.plenti_server.dto.scrap.ScrapReqDto;
import com.plenti.plenti_server.dto.scrap.ScrapDelQuizDto;
import com.plenti.plenti_server.dto.scrap.ScrapAddQuizDto;
import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Scrap;
import com.plenti.plenti_server.entity.Quiz;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.ForbiddenException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import com.plenti.plenti_server.repository.MemberRepository;
import com.plenti.plenti_server.repository.QuizRepository;
import com.plenti.plenti_server.repository.ScrapRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScrapService {

  private final MemberRepository memberRepository;
  private final ScrapRepository scrapRepository;
  private final QuizRepository quizRepository;

  public ScrapService(
    ScrapRepository scrapRepository,
    MemberRepository memberRepository,
    QuizRepository quizRepository
  ) {
    this.scrapRepository = scrapRepository;
    this.memberRepository = memberRepository;
    this.quizRepository = quizRepository;
  }

  @Transactional
  public List<MyScrapListResDto> getMyScrapList(String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    List<Scrap> scrapList = scrapRepository.findAllByScrapUser(member);
    List<MyScrapListResDto> myScrapList = scrapList
      .stream()
      .map(scrap -> MyScrapListResDto.from(scrap))
      .collect(Collectors.toList());

    return myScrapList;
  }

  @Transactional
  public SuccessDto deleteScrap(Long scrapId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    Scrap scrap = scrapRepository
      .findById(scrapId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 스크랩입니다.")
      );
    if (scrap.getScrapUser().getMemberId() == member.getMemberId()) {
      scrapRepository.delete(scrap);
      return new SuccessDto(200, "success");
    }
    throw new UnauthorizedException("이 스크랩에 대한 삭제 권한이 없습니다.");
  }

  @Transactional
  public SuccessDto modifyScrap(
    ScrapReqDto scrapReqDto,
    Long scrapId,
    String userEmail
  ) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    Scrap scrap = scrapRepository
      .findById(scrapId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 스크랩입니다.")
      );
    if (scrap.getScrapUser().getMemberId() != member.getMemberId()) {
      throw new UnauthorizedException("수정할 수 없는 스크랩 정보입니다.");
    }
    scrap.setScrapNm(scrapReqDto.getScrapNm());
    scrapRepository.save(scrap);
    return new SuccessDto(200, "success!");
  }

  @Transactional
  public SuccessDto createScrap(ScrapReqDto scrapReqDto, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Scrap scrap = Scrap
      .builder()
      .scrapNm(scrapReqDto.getScrapNm())
      .scrapUser(member)
      .build();

    scrapRepository.save(scrap);

    return new SuccessDto(200, "success!");
  }

  @Transactional
  public ScrapDetailResDto getScrapDetail(Long scrapId, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );
    Scrap scrap = scrapRepository
      .findById(scrapId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 스크랩입니다.")
      );
    if (scrap.getScrapUser().getMemberId() == member.getMemberId()) {
      return ScrapDetailResDto.from(scrap);
    }
    throw new ForbiddenException("이 스크랩을 볼 수 있는 권한이 없습니다.");
  }

   @Transactional
  public SuccessDto addQuiz(ScrapAddQuizDto scrapAddQuizDto, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Scrap scrap = scrapRepository
      .findById(scrapAddQuizDto.getScrapId())
      .orElseThrow(() -> new BadRequestException("존재하지 않는 스크랩입니다.")
      );

    Quiz quiz = quizRepository
      .findById(scrapAddQuizDto.getQuizId())
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다.")
      );

    if (scrap.getScrapUser().getMemberId() == member.getMemberId()) {
      List<Quiz> quizes = scrap.getScrapQuizes();
      quizes.add(quiz);
      scrap.setScrapQuizes(quizes);

      scrapRepository.save(scrap);
      return new SuccessDto(200, "success!");
    }
    throw new ForbiddenException("이 스크랩을 볼 수 있는 권한이 없습니다.");
  }
  
   @Transactional
  public SuccessDto scrapDelQuiz(ScrapDelQuizDto scrapDelQuizDto, String userEmail) {
    Member member = memberRepository
      .findOneByEmail(userEmail)
      .orElseThrow(() ->
        new UnauthorizedException("존재하지 않는 회원정보입니다.")
      );

    Scrap scrap = scrapRepository
      .findById(scrapDelQuizDto.getScrapId())
      .orElseThrow(() -> new BadRequestException("존재하지 않는 스크랩입니다.")
      );

    Quiz quiz = quizRepository
      .findById(scrapDelQuizDto.getQuizId())
      .orElseThrow(() -> new BadRequestException("존재하지 않는 퀴즈입니다.")
      );

    if (scrap.getScrapUser().getMemberId() == member.getMemberId()) {
      List<Quiz> quizList = scrap.getScrapQuizes();
      quizList.remove(quiz);
      scrap.setScrapQuizes(quizList);

      scrapRepository.save(scrap);

      return new SuccessDto(200, "success!");
    }
        
    throw new ForbiddenException("이 스크랩을 볼 수 있는 권한이 없습니다.");
  }
}
