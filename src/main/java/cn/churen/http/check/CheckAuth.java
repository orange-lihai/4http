package cn.churen.http.check;

import cn.churen.http.result.Result;
import cn.churen.http.result.ResultCode;
import org.glassfish.grizzly.http.server.Request;

import java.util.Map;

public class CheckAuth implements ICheck {

  public static Result<Boolean> check(Request request) {
    String oath = request.getAuthorization();
    Map<String, String[]> params = request.getParameterMap();

    return new Result<>(true, ResultCode.AUTH_OK, "");
  }
}
