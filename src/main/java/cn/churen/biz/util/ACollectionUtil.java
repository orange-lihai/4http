package cn.churen.biz.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ACollectionUtil {

  public static boolean isAnyEmpty(Object... args) {
    for (Object o : args) {
      if (null == o
          || (o instanceof CharSequence && StringUtils.isBlank((CharSequence) o))
          || (o instanceof Collection && ((Collection) o).isEmpty())
      ) {
        return true;
      }
    }
    return false;
  }

  public static boolean isAnyNull(Object... args) {
    for (Object o : args) {
      if (null == o) {
        return true;
      }
    }
    return false;
  }

  public static boolean isNoneEmpty(Object... args) {
    return !isAnyEmpty(args);
  }

  public static <T, R> R evalFirstIfExists(List<T> list, Function<T, R> func) {
    if (CollectionUtils.isNotEmpty(list)) {
      T t = list.get(0);
      if (null != t && null != func) {
        return func.apply(t);
      }
    }

    return null;
  }

  public static <T, R> List<R> evalIfExists(Collection<T> list, Function<T, R> func) {
    List<R> rs = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(list)) {
      list.stream().filter(Objects::nonNull).forEach(t -> {
        R r = func.apply(t);
        if (null != r) { rs.add(r); }
      });
    }
    return rs;
  }
}
