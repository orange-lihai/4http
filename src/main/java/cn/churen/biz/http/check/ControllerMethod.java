package cn.churen.biz.http.check;

import java.lang.reflect.Method;
import java.util.*;

public class ControllerMethod {
  private Method method;
  private LinkedHashMap<String, Class> paramsMap;
  private Class<?> returnType;

  private Class<?> clazz;
  private Object clazzInstance;

  public ControllerMethod(Method m) {
    this.method = m;
    this.returnType = m.getReturnType();
    this.paramsMap = new LinkedHashMap<>();
    this.clazz = m.getDeclaringClass();
    this.clazzInstance = CheckUtil.createInstance(clazz);
  }

  @SuppressWarnings({"unchecked"})
  public Object[] matchParameters(Map<String, String> allRequestParams, String jsonOrXmlEtc) {
    List<Object> rs = new ArrayList<>();
    for (String name : paramsMap.keySet()) {
      Class zz = paramsMap.get(name);
      if (CheckUtil.isPrimitive(zz)
          || zz.isAssignableFrom(Date.class)
          || zz.isAssignableFrom(String.class)
          || zz.isAssignableFrom(StringBuffer.class)
          || zz.isAssignableFrom(StringBuilder.class)
          || zz.isArray()) {
        Object o = CheckUtil.create(zz, allRequestParams.get(name));
        rs.add(o);
      } else {
        Object o = CheckUtil.create(zz, jsonOrXmlEtc);
        rs.add(o);
      }
    }
    return rs.toArray();
  }

  // setters and getters
  public Class<?> getReturnType() {
    return returnType;
  }

  public void setReturnType(Class<?> returnType) {
    this.returnType = returnType;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public LinkedHashMap<String, Class> getParamsMap() {
    return paramsMap;
  }

  public void setParamsMap(LinkedHashMap<String, Class> paramsMap) {
    this.paramsMap = paramsMap;
  }

  public Class getClazz() {
    return clazz;
  }

  public void setClazz(Class clazz) {
    this.clazz = clazz;
  }

  public Object getClazzInstance() {
    return clazzInstance;
  }

  public void setClazzInstance(Object clazzInstance) {
    this.clazzInstance = clazzInstance;
  }

}
