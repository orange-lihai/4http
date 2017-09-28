package cn.churen.http.check;

import cn.churen.http.result.Result;
import cn.churen.http.result.ResultCode;
import com.google.common.collect.Sets;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.server.Request;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckMethodInvoke {
  public static final Logger logger = Grizzly.logger(CheckMethodInvoke.class);
  private static CheckMethodInvoke _this;
  private static Map<String, ControllerMethod> controllerMethodMap = new HashMap<>();
  private static Map<String, Object> serviceNameMap = new HashMap<>();
  private static Map<Class, Object> serviceClazzMap = new HashMap<>();

  private CheckMethodInvoke() {
    matchAllControllerMethod();
  }
  private Map<String, ControllerMethod> matchAllControllerMethod() {
    ConfigurationBuilder methodConfigBuilder =
        new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("cn.churen.controller"))
                                  .setScanners(new MethodAnnotationsScanner());
    Reflections methodReflections = new Reflections(methodConfigBuilder);

    ConfigurationBuilder methodParameterConfigBuilder =
        new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("cn.churen.controller"))
                                  .setScanners(new MethodParameterNamesScanner());
    Reflections parameterReflections = new Reflections(methodParameterConfigBuilder);

    Set<Method> methodSet = Sets.newHashSet(
        methodReflections.getMethodsAnnotatedWith(AControllerMapping.class)
    );

    for (Method e : methodSet) {
      // Parameter[] parameters = e.getParameters();
      if (!e.getDeclaringClass().isAnnotationPresent(AController.class)) { continue; }
      String[] uriArr = e.getAnnotation(AControllerMapping.class).uri();
      for (String _uri : uriArr) {
        ControllerMethod cm = new ControllerMethod(e);
        List<String> parameterNames = parameterReflections.getMethodParamNames(e);
        Parameter[] parameters = e.getParameters();
        for (int i = 0; null != parameterNames && i < parameters.length; i++) {
          cm.getParamsMap().put(parameterNames.get(i), parameters[i].getType());
        }

        controllerMethodMap.put(_uri, cm);
      }
    }
    return controllerMethodMap;
  }
  private static ControllerMethod matchControllerMethod(String uri) {
    return controllerMethodMap.get(uri);
  }

  public static Object setServiceNameMap(String name, Object service) {
     return serviceNameMap.put(name, service);
  }
  public static Object getServiceNameMap(String name) {
    return serviceNameMap.get(name);
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T setServiceClazzMap(Class<T> clazz, Object service) {
    if (null != clazz && null != service && service.getClass().isAssignableFrom(clazz)) {
      return (T) serviceClazzMap.put(clazz, service);
    } else {
      return null;
    }
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T getServiceClazzMap(Class<T> tClass) {
    return (T) serviceClazzMap.get(tClass);
  }

  public static CheckMethodInvoke getInstance() {
    if (null == _this) {
      synchronized (CheckMethodInvoke.class) {
        if (null == _this) {
          _this = new CheckMethodInvoke();
        }
      }
    }
    return _this;
  }

  public Result<ControllerMethod> check(Request request) {
    Result<ControllerMethod> r = new Result<>(true);
    try {
      String uri = request.getRequestURI();
      r.data = matchControllerMethod(uri);
      r.resultCode = ResultCode.METHOD_INVOKE_OK;
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
      r = new Result<>(false, ResultCode.METHOD_INVOKE_ERROR, ex.getMessage());
    }
    return r;
  }


}
