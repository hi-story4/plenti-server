package com.plenti.plenti_server.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizItem {

  @NotNull
  @NotEmpty
  private String question;

  @NotNull
  @NotEmpty
  private String answer;
}
