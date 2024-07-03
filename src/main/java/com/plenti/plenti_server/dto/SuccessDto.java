package com.plenti.plenti_server.dto;

public class SuccessDto {

  private final int status;
  private final String message;

  public SuccessDto(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
