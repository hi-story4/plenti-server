package com.plenti.plenti_server.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass // 맴버변수가 컬럼이 되도록
@EntityListeners(AuditingEntityListener.class) // 변경되었을 때 자동으로 기록합니다.
public class Timestamped {

  @CreatedDate // 최초 생성 시점
  private LocalDateTime createdAt;

  @LastModifiedDate // 마지막 변경 시점
  private LocalDateTime modifiedAt;

  //삭제 시점
  private LocalDateTime deletedAt;
}
