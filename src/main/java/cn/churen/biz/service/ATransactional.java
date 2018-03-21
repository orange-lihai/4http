package cn.churen.biz.service;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATransactional {
  String[] before() default {};
  String[] end() default {};
}
