package cn.churen.util;

import com.google.common.collect.Maps;
import net.sf.cglib.beans.BeanMap;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BUtil {

  public static Map<String, String> toMap(String... keysAndValues) {
    Map<String, String> r = new HashMap<>();
    for (int i = 0; null != keysAndValues && i < keysAndValues.length; i += 2) {
      r.put(keysAndValues[i], keysAndValues[i + 1]);
    }
    return r;
  }

  public static <T> Map<String, Object> beanToMap(T bean) {
    Map<String, Object> map = Maps.newHashMap();
    if (bean != null) {
      BeanMap beanMap = BeanMap.create(bean);
      for (Object key : beanMap.keySet()) {
        map.put(key+"", beanMap.get(key));
      }
    }
    return map;
  }

  public static <T> T mapToBean(Map<String, Object> map, Class<T> bean) {
    Object obj = null;
    try {
      if (map == null) {
        return null;
      }
      obj = bean.newInstance();

      BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor property : propertyDescriptors) {
        Method setter = property.getWriteMethod();
        if (setter != null) {
          setter.invoke(obj, map.get(property.getName()));
        }
      }

    } catch (Exception ex) {

    }
    return (T) obj;
  }
}
