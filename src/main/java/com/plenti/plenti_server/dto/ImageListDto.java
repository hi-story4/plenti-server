package com.plenti.plenti_server.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageListDto {

  private List<String> files;

  public static ImageListDto from(List<String> files) {
    if (files == null) return null;

    return ImageListDto.builder().files(files).build();
  }
}
