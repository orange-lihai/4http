package cn.churen.biz.http.handler;

import cn.churen.biz.http.check.*;
import cn.churen.biz.http.result.Result;
import cn.churen.biz.util.AContextHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IndexHandler extends HttpHandler {

  @Override public void service(Request request, Response response) throws Exception {
    AContextHolder.clear();

    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Headers"
        , "Origin, X-Requested-With, Content-Type, Accept, Cookie, userToken");

    Result<CheckStaticFile.StaticFile> staticFileCheckResult = CheckStaticFile.check(request);
    if (staticFileCheckResult.data.isStaticFile) {
      writeResponse(request, response, staticFileCheckResult.data);
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

  private void writeResponse(Request request, Response response, CheckStaticFile.StaticFile staticFile
  ) throws IOException {
    response.setContentType(staticFile.contentType);
    String fileString = "file not found!!!";
    InputStream is = null;
    try {
      is = this.getClass().getClassLoader().getResourceAsStream("web/" + staticFile.fileFullName);
      if (null == is) {
        fileString = PageGenerator.generatePage(request);
      } else {
        fileString = IOUtils.toString(is, "utf-8");
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    } finally {
      if (null != is) { is.close(); }
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

  public static Map<String, String> getParameters(Request request) {
    Map<String, String> map = AContextHolder.get(AContextHolder.RC.PARAMETER.name(), Map.class);
    if (null == map) {
      map = new HashMap<>();
      Map<String, String[]> parameters = request.getParameterMap();
      for (String k : parameters.keySet()) {
        String[] v = parameters.get(k);
        map.put(k, StringUtils.join(Arrays.asList(v), ""));
      }
      AContextHolder.set(AContextHolder.RC.PARAMETER.name(), map);
    }
    return map;
  }

  public static String getRaw(Request request) {
    String raw = AContextHolder.getOrDefault(AContextHolder.RC.RAW.name(), String.class, "{}");
    try {
      if (StringUtils.isBlank(raw)) {
        String contentType = request.getContentType();
        int len = request.getContentLength();
        if (len >= 1024 * 10) {
          throw new IOException("ContentLength is too big!!!");
        }
        if (len > 0) {
          char buf[] = new char[len];
          if (CheckUtil.isRaw(contentType)) {
            request.getReader().read(buf);
            raw = String.valueOf(buf);
          } else {
            raw = "{}";
          }
        } else {
          raw = "{}";
        }
      }
    } catch (Exception ex) {
      raw = "{}";
      log.error(ex.getMessage(), ex);
    }
    return raw;
  }
}
