package com.plenti.plenti_server.security;

import com.plenti.plenti_server.domain.AuthRoleEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Auth {
  AuthRoleEnum role() default AuthRoleEnum.ROLE_USER;
}
