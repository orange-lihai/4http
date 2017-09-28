package cn.churen.http.check;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ControllerInvocationHandler<T> implements InvocationHandler {
  @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return method.invoke(proxy, args);
  }
}
