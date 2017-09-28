package cn.churen.service;


import java.lang.annotation.*;
import java.sql.Connection;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATransactional {
  int level() default Connection.TRANSACTION_READ_COMMITTED;
  
  String[] before() default {};
  String[] end() default {};
}
