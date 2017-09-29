package cn.churen.service;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ALog {
  String beforeLog() default "";
  String afterLog() default "";
  String logLevel() default "INFO";
}
