package cn.churen.biz.http.check;

import cn.churen.biz.dao.crud.table.ATableColumn;
import cn.churen.biz.service.*;
import cn.churen.biz.util.AContextHolder;
import net.sf.cglib.core.internal.Function;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.Grizzly;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceProxy implements MethodInterceptor {
  private Logger logger = Grizzly.logger(ServiceProxy.class);

  @Override public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
      throws Throwable {
    InterceptParameter interceptParameter = new InterceptParameter(obj, method, args, proxy);
    Function<InterceptParameter, Object> f = interceptInit(interceptParameter);
    
    Annotation[] annotations = method.getDeclaredAnnotations();
    for (int i = annotations.length - 1; i >= 0; i--) {
      Annotation an = annotations[i];
      f = wrapperWithAnnotation(f, interceptParameter, an);
    }

    return f.apply(interceptParameter);
  }

  private Function<InterceptParameter, Object> interceptInit(InterceptParameter p) {
    return (InterceptParameter pa) -> {
      try {
        return p.proxy.invokeSuper(p.obj, p.args);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        return null;
      }
    };
  }

  private Function<InterceptParameter, Object> wrapperWithAnnotation (
      Function<InterceptParameter, Object> sourceFunc
      , InterceptParameter sourceFuncParameter
      , Annotation an
  ) throws Throwable {
    return (p) -> {
      try {
        if (an instanceof ALog) {
          return invokeWithALog((ALog) an, () -> sourceFunc.apply(sourceFuncParameter));
        } else if (an instanceof ATransactional) {
          return invokeWithATransactional(() -> sourceFunc.apply(sourceFuncParameter));
        } else if (an instanceof ADao) {
          return appendResultToADao(sourceFunc.apply(sourceFuncParameter), sourceFuncParameter);
        } else {
          return sourceFunc.apply(sourceFuncParameter);
        }
      } catch (Throwable throwable) {
        AContextHolder.rollbackConnection();
        logger.log(Level.SEVERE, throwable.getMessage(), throwable.fillInStackTrace());
        return null;
      }
    };
  }

  private Object appendResultToADao(Object initRs, InterceptParameter sourceFuncParameter) {
    List<Object> rs = new ArrayList<>();
    if (initRs instanceof Collection) {
      rs.addAll((Collection<?>) initRs);
    } else {
      rs.add(initRs);
    }

    Statement statement = null;
    Connection conn = null;
    try {
      Object[] args = sourceFuncParameter.args;
      Parameter[] parameters = sourceFuncParameter.method.getParameters();
      List<String> sqlList = new ArrayList<>();
      for (int i = 0; i < parameters.length; i++) {
        Parameter p = parameters[i];
        if (p.isAnnotationPresent(ASql.class)) {
          sqlList.add(String.valueOf(args[i]));
        }
      }
      conn = AContextHolder.getConnection();
      statement = conn.createStatement();
      for (String s : sqlList) {
        ResultSet _rs = statement.executeQuery(s);
        Class<?> _clazz = getADaoMethodReturnType(sourceFuncParameter);
        rs.addAll(getADaoMethodObject(_rs, _clazz));
        _rs.close();
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    } finally {
      try {
        if (null != statement && !statement.isClosed()) {
          statement.close();
        }
        if (null != conn) { conn.close(); }
      } catch (SQLException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    }
    return rs;
  }

  private <T> Collection<T> getADaoMethodObject(ResultSet rs, Class<T> clazz) throws SQLException {
    List<T> list = new ArrayList<>();
    while (rs.next()) {
      Map<String, Object> m = new HashMap<>();
      int len = rs.getMetaData().getColumnCount();
      for (int i = 0; i < len; i++) {
        String k = rs.getMetaData().getColumnName(i + 1);
        Object v = rs.getObject(i + 1);
        m.put(k, v);
      }
      list.add(mapToBean(m, clazz));
    }
    return list;
  }

  private  <T> T mapToBean(Map<String, Object> map, Class<T> bean) {
    T obj = null;
    try {
      obj = bean.newInstance();
      Field[] fields = bean.getDeclaredFields();
      for (Field f : fields) {
        if (f.getModifiers() >= 8) { continue; }
        String name = f.getName();
        ATableColumn columnAnnotation = f.getAnnotation(ATableColumn.class);
        if (null != columnAnnotation) {
          name = columnAnnotation.name();
        }
        if (!map.containsKey(name)) { continue; }
        f.setAccessible(true);
        f.set(obj, map.get(name));
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
    return obj;
  }

  private Class getADaoMethodReturnType(InterceptParameter parameter) {
    Method method = parameter.method;
    Object[] args = parameter.args;
    Parameter[] ps = method.getParameters();
    for (int i = 0; i < ps.length; i++) {
      if (ps[i].isAnnotationPresent(AResultType.class)) {
        return (Class) args[i];
      }
    }
    return Map.class;
  }

  private Object invokeWithALog(ALog aLog, Callable callable) {
    Object o = null;
    try {
      String message = aLog.beforeLog();
      if (StringUtils.isNotEmpty(message)) {
        logger.log(Level.parse(aLog.logLevel()), message);
      }
      o = callable.call();
      message = aLog.afterLog();
      if (StringUtils.isNotEmpty(message)) {
        logger.log(Level.parse(aLog.logLevel()), message);
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
    return o;
  }

  private Object invokeWithATransactional(Callable callable) {
    Object o = null;
    Connection conn = null;
    try {
      Boolean tranExists = AContextHolder.getOrDefault(
          AContextHolder.RC.TRANSACTION.name(), Boolean.class, false);
      if (tranExists) {
        o = callable.call();
      } else {
        conn = AContextHolder.getConnection();
        conn.setAutoCommit(false);
        AContextHolder.set(AContextHolder.RC.TRANSACTION.name(), true);
        o = callable.call();
        conn.commit();
      }
    } catch (Throwable throwable) {
      try {
        if (null != conn) {
          conn.rollback();
        }
      } catch (SQLException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    } finally {
      try {
        if (null != conn) { conn.close(); }
        AContextHolder.set(AContextHolder.RC.TRANSACTION.name(), false);
      } catch (SQLException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    }
    return o;
  }


  /**
   * inner class, struct for arguments of intercept function
   */
  class InterceptParameter {
    Object obj;
    Method method;
    Object[] args;
    MethodProxy proxy;

    InterceptParameter(Object obj, Method method, Object[] args, MethodProxy proxy) {
      this.obj = obj;
      this.method = method;
      this.args = args;
      this.proxy = proxy;
    }
  }
}
