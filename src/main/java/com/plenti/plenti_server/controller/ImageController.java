package com.plenti.plenti_server.controller;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import com.plenti.plenti_server.dto.ImageListDto;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.security.Auth;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

  @PostMapping("/uploadFiles")
  @Auth(role = AuthRoleEnum.ROLE_USER)
  public ResponseEntity<Object> uploadFiles(MultipartFile[] multipartFiles) { // 파라미터의 이름은 client의 formData key값과 동일해야함
    String UPLOAD_PATH = new File("").getAbsolutePath() + "\\Images";
    long fileMaxSize = 6000000;

    List<String> files = new ArrayList<String>();
    try {
      for (int i = 0; i < multipartFiles.length; i++) {
        MultipartFile file = multipartFiles[i];

        String fileId =
          (new Date().getTime()) +
          "" +
          (new Random().ints(1000, 9999).findAny().getAsInt()); // 현재 날짜와 랜덤 정수값으로 새로운 파일명 만들기
        String originName = file.getOriginalFilename(); // ex) 파일.jpg
        String fileExtension = originName.substring(
          originName.lastIndexOf(".") + 1
        ); // ex) jpg

        if (StringUtils.hasText(fileExtension)) {
          if (
            !fileExtension.equals("jpeg") &&
            !fileExtension.equals("jpg") &&
            !fileExtension.equals("png") &&
            !fileExtension.equals("gif")
          ) {
            throw new BadRequestException(
              Integer.toString(i) +
              "번째 이미지가 허용되지 않은 확장자를 가지고 있습니다."
            );
          }
        } else {
          throw new BadRequestException(
            Integer.toString(i) + "번째 이미지가 확장자를 가지고 있지 않습니다."
          );
        }
        originName = originName.substring(0, originName.lastIndexOf(".")); // ex) 파일
        long fileSize = file.getSize(); // 파일 사이즈
        if (fileSize > fileMaxSize) {
          throw new BadRequestException(
            Integer.toString(i) +
            "번째 이미지가 허용되는 크기 이상의 파일 크기를 가지고 있습니다."
          );
        }

        System.out.println(fileSize);

        File fileSave = new File(UPLOAD_PATH, fileId + "." + fileExtension); // ex) fileId.jpg
        if (!fileSave.exists()) { // 폴더가 없을 경우 폴더 만들기
          fileSave.mkdirs();
        }

        file.transferTo(fileSave); // fileSave의 형태로 파일 저장
        files.add(fileId + "." + fileExtension);
      }
    } catch (IOException e) {
      return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
    }

    return new ResponseEntity<Object>(ImageListDto.from(files), HttpStatus.OK);
  }

  @GetMapping(
    value = "image/{imagename}",
    produces = MediaType.IMAGE_JPEG_VALUE
  )
  public ResponseEntity<byte[]> userSearch(
    @PathVariable("imagename") String imagename
  ) throws IOException {
    String IMAGE_PATH = new File("").getAbsolutePath() + "\\Images\\";
    InputStream imageStream = new FileInputStream(IMAGE_PATH + imagename);
    byte[] imageByteArray = IOUtils.toByteArray(imageStream);
    imageStream.close();
    return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
  }
}
