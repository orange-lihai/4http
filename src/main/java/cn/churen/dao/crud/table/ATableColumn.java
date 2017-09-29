package cn.churen.dao.crud.table;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATableColumn {
  String name();
}
