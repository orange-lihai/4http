package cn.churen.service;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ALog {
  String message() default "";

  String[] before() default {};
  String[] end() default {};
}
