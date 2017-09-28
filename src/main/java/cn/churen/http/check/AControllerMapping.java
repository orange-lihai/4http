package cn.churen.http.check;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AControllerMapping {
  String[] uri() default {};

  String[] method() default {};
}
