package cn.churen.biz.http.check;

import com.google.gson.GsonBuilder;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.util.FastDateFormat;

import java.lang.reflect.Field;
import java.util.Date;

public class CheckUtil {
  public static boolean isRaw(String contentType) {
    return contentType.contains("application/json")
           || contentType.contains("application/xml");
  }

  @SuppressWarnings({"unchecked"})
  public static boolean isPrimitive(Class zz) {
    return null != zz && (zz.isPrimitive()
                          || zz.isAssignableFrom(Boolean.class)
                          || zz.isAssignableFrom(Character.class)
                          || zz.isAssignableFrom(Byte.class)
                          || zz.isAssignableFrom(Short.class)
                          || zz.isAssignableFrom(Integer.class)
                          || zz.isAssignableFrom(Long.class)
                          || zz.isAssignableFrom(Float.class)
                          || zz.isAssignableFrom(Double.class));
  }

  public static Object createInstance(Class<?> clazz) {
    Object o = null;
    try {
      o = MethodInvoke.getServiceClazzMap(clazz);
      if (null == o) {
        if (clazz.isAnnotationPresent(AController.class)) {
          o = clazz.newInstance();
        } else if (clazz.isAnnotationPresent(AService.class)) {
          ServiceProxy cglibProxy = new ServiceProxy();
          Enhancer enhancer = new Enhancer();
          enhancer.setSuperclass(clazz);
          enhancer.setCallback(cglibProxy);
          o = enhancer.create();
        } else {
          o = clazz.newInstance();
        }
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; null != fields && i < fields.length; i++) {
          Field f = fields[i];
          if (f.getType().isAnnotationPresent(AService.class) && f.isAnnotationPresent(AInjection.class)) {
            Object fo = createInstance(f.getType());
            f.setAccessible(true);
            f.set(o, fo);
          }
        }
        MethodInvoke.setServiceClazzMap(clazz, o);
        MethodInvoke.setServiceNameMap(clazz.getSimpleName(), o);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return o;
  }

  @SuppressWarnings({"unchecked"})
  public static Object create(Class zz, String s) {
    if (StringUtils.isEmpty(s)) { return s; }
    Object o = null;
    try {
      if (zz == Boolean.class || zz == boolean.class) {
        o = Boolean.valueOf(s);
      }
      if (zz == Character.class || zz == char.class) {
        o = s.charAt(0);
      }
      if (zz == Byte.class || zz == byte.class) {
        o = s.getBytes();
      }
      if (zz == Short.class || zz == short.class) {
        o = Short.valueOf(s);
      }
      if (zz == Integer.class || zz == int.class) {
        o = Integer.valueOf(s);
      }
      if (zz == Long.class || zz == long.class) {
        o = Long.valueOf(s);
      }
      if (zz == Float.class || zz == float.class) {
        o = Float.valueOf(s);
      }
      if (zz == Double.class || zz == double.class) {
        o = Double.valueOf(s);
      }

      if (zz == String.class) {
        o = String.valueOf(s);
      } else if (zz.isAssignableFrom(StringBuffer.class)) {
        o = new StringBuffer(s);
      } else if (zz.isAssignableFrom(StringBuilder.class)) {
        o = new StringBuilder(s);
      } else if (zz.isAssignableFrom(Date.class)) {
        o = FastDateFormat.getInstance().parse(s);
      } else if (zz.isArray()) {
        o = s.split(",");
      } else {
        o = new GsonBuilder().create().fromJson(s, zz);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return o;
  }
}
