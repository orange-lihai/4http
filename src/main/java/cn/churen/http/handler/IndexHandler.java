package cn.churen.http.handler;

import cn.churen.http.check.*;
import cn.churen.http.result.Result;
import cn.churen.http.result.ResultCode;
import cn.churen.util.ContextHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.deploy.util.StringUtils;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndexHandler extends HttpHandler {
  public static final Logger logger = Grizzly.logger(IndexHandler.class);

  @Override public void service(Request request, Response response) throws Exception {
    ContextHolder.clear();

    Result<Boolean> httpCheckResult = CheckHTTP.check(request);
    if (!httpCheckResult.success) {
      writeResponse(response, httpCheckResult);
      return;
    }

    Result<Boolean> authCheckResult = CheckAuth.check(request);
    if (!authCheckResult.success) {
      writeResponse(response, authCheckResult);
      return;
    }

    CheckMethodInvoke checkMethodInvoke = CheckMethodInvoke.getInstance();
    Result<ControllerMethod> methodInvokeResult = checkMethodInvoke.check(request);
    if (!methodInvokeResult.success) {
      writeResponse(response, methodInvokeResult);
      return;
    }

    Result<Object> r = new Result<>(true);
    try {
      ControllerMethod m = methodInvokeResult.data;
      Map<String, String> allRequestParams = getParameters(request);
      String jsonOrXmlEtc = getRaw(request);

      Object[] params = m.matchParameters(allRequestParams, jsonOrXmlEtc);
      r.data = m.getMethod().invoke(m.getClazzInstance(), params);
      r.success = true;
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
      r = new Result<>(false, ResultCode.REQUEST_ERROR, ex.getMessage());
    } finally {
      writeResponse(response, r);
    }
  }

  private <D> void writeResponse(Response response, Result<D> result) throws IOException {
    response.setContentType("application/json");
    Gson gson = new GsonBuilder().create();

    response.getWriter().write(gson.toJson(result));
    response.flush();
  }

  private Map<String, String> getParameters(Request request) {
    Map<String, String> map = new HashMap<>();
    Map<String, String[]> parameters = request.getParameterMap();
    for (String k : parameters.keySet()) {
      String[] v = parameters.get(k);
      map.put(k, StringUtils.join(Arrays.asList(v), ""));
    }
    return map;
  }

  private String getRaw(Request request) throws IOException {
    String contentType = request.getContentType();
    int len = request.getContentLength();
    if (len >= 1024 * 10) { throw new IOException("ContentLength is too big!!!"); }
    char buf[] = new char[len];
    if (CheckUtil.isRaw(contentType)) {
      request.getReader().read(buf);
      return String.valueOf(buf);
    } else {
      return "";
    }
  }
}
