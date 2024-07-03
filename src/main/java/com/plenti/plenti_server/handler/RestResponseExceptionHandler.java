package com.plenti.plenti_server.handler;

import static org.springframework.http.HttpStatus.*;

import com.plenti.plenti_server.dto.ErrorDto;
import com.plenti.plenti_server.exception.BadRequestException;
import com.plenti.plenti_server.exception.DuplicateMemberException;
import com.plenti.plenti_server.exception.ForbiddenException;
import com.plenti.plenti_server.exception.UnauthorizedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler
  extends ResponseEntityExceptionHandler {

  @ResponseStatus(CONFLICT)
  @ExceptionHandler(value = { DuplicateMemberException.class })
  @ResponseBody
  protected ErrorDto conflict(RuntimeException ex, WebRequest request) {
    return new ErrorDto(CONFLICT.value(), ex.getMessage());
  }

  @ResponseStatus(FORBIDDEN)
  @ExceptionHandler(
    value = { ForbiddenException.class, AccessDeniedException.class }
  )
  @ResponseBody
  protected ErrorDto forbidden(RuntimeException ex, WebRequest request) {
    return new ErrorDto(FORBIDDEN.value(), ex.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(value = { BadRequestException.class })
  @ResponseBody
  protected ErrorDto badRequest(RuntimeException ex, WebRequest request) {
    return new ErrorDto(BAD_REQUEST.value(), ex.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(value = { UnauthorizedException.class })
  @ResponseBody
  protected ErrorDto unauthorized(RuntimeException ex, WebRequest request) {
    return new ErrorDto(UNAUTHORIZED.value(), ex.getMessage());
  }
}
