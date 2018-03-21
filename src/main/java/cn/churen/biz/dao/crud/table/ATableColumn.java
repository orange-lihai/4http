package cn.churen.biz.dao.crud.table;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATableColumn {
  String name();
}
