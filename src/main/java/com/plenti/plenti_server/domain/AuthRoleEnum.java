package com.plenti.plenti_server.domain;

import lombok.Getter;

@Getter
public enum AuthRoleEnum {
  ROLE_ADMIN("관리자"),
  ROLE_USER("사용자");

  private final String description;

  AuthRoleEnum(String description) {
    this.description = description;
  }
}
