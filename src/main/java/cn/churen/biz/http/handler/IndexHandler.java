package cn.churen.biz.http.handler;

import cn.churen.biz.http.check.*;
import cn.churen.biz.http.result.Result;
import cn.churen.biz.util.ContextHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IndexHandler extends HttpHandler {

  @Override public void service(Request request, Response response) throws Exception {
    ContextHolder.clear();

    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Headers"
        , "Origin, X-Requested-With, Content-Type, Accept, Cookie, userToken");

    Result<CheckStaticFile.StaticFile> staticFileCheckResult = CheckStaticFile.check(request);
    if (staticFileCheckResult.data.isStaticFile) {
      writeResponse(response, staticFileCheckResult.data);
      return;
    }

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

    Result<Object> r = Result.build(() -> {
      MethodInvoke methodInvoke = MethodInvoke.getInstance();
      Result<ControllerMethod> methodInvokeResult = methodInvoke.check(request);
      if (!methodInvokeResult.success) {
        return methodInvokeResult;
      }
      ControllerMethod m = methodInvokeResult.data;
      Map<String, String> allRequestParams = getParameters(request);
      String jsonOrXmlEtc = getRaw(request);

      Object[] params = m.matchParameters(allRequestParams, jsonOrXmlEtc);
      return m.getMethod().invoke(m.getClazzInstance(), params);
    });
    writeResponse(response, r);
  }

  private void writeResponse(Response response, CheckStaticFile.StaticFile staticFile
  ) throws IOException {
    response.setContentType(staticFile.contentType);
    String fileString = "file not found!!!";
    try {
      fileString = IOUtils.toString(
          this.getClass().getClassLoader().getResourceAsStream("web/" + staticFile.fileFullName)
          , "utf-8"
      );
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    response.getWriter().write(fileString);
    response.flush();
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
    if (len > 0) {
      char buf[] = new char[len];
      if (CheckUtil.isRaw(contentType)) {
        int n = request.getReader().read(buf);
        if (n >= 0) {
          log.error("content length: " + n);
        }
        return String.valueOf(buf);
      } else {
        return "{}";
      }
    } else {
      return "{}";
    }
  }
}
