package cn.churen.http.check;

import cn.churen.http.result.Result;
import cn.churen.http.result.ResultCode;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.Request;

public class CheckHTTP implements ICheck {

  public static Result<Boolean> check(Request request) {
    String url = request.getRequestURI();
    Method httpMethod = request.getMethod();

    return new Result<>(true, ResultCode.HTTP_METHOD_OK, "");
  }
}
