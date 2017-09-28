package cn.churen.http.check;

import cn.churen.service.ALog;
import cn.churen.service.ATransactional;
import cn.churen.util.ContextHolder;
import net.sf.cglib.core.internal.Function;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.Grizzly;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
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
        if (an.getClass().isAssignableFrom(ALog.class)) {
          return invokeWithALog((ALog) an, () -> sourceFunc.apply(sourceFuncParameter));
        } else if (an.getClass().isAssignableFrom(ATransactional.class)) {
          return invokeWithATransactional((ATransactional) an, () -> sourceFunc.apply(sourceFuncParameter));
        } else {
          return sourceFunc.apply(sourceFuncParameter);
        }
      } catch (Throwable throwable) {
        ContextHolder.rollbackConnection();
        logger.log(Level.SEVERE, throwable.getMessage(), throwable.fillInStackTrace());
        return null;
      }
    };
  }

  private Object invokeWithALog(ALog aLog, Callable callable) {
    Object o = null;
    try {
      // TODO log
      String message = aLog.log();
      if (!StringUtils.isNotEmpty(message)) {
        logger.log(Level.INFO, message);
      }
      // TODO beforeFunc
      String[] beforeFunc = aLog.before();
      o = callable.call();
      // TODO afterFunc
      String[] afterFunc = aLog.after();
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
    return o;
  }

  private Object invokeWithATransactional(ATransactional an, Callable callable) {
    Object o = null;
    Connection conn = null;
    try {
      conn = ContextHolder.getConnection();
      conn.setAutoCommit(false);
      conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      ContextHolder.set(ContextHolder.RC.TRANSACTION.name(), true);
      o = callable.call();
      conn.commit();
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
        ContextHolder.set(ContextHolder.RC.TRANSACTION.name(), false);
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
