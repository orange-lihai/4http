package cn.churen.http.check;

import cn.churen.http.result.Result;
import cn.churen.util.BUtil;
import org.glassfish.grizzly.http.server.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CheckStaticFile implements ICheck {
  public static List<String> fileSuffixList = Arrays.asList(
      ".html", ".htm", ".xhtml"
      , ".css", ".less"
      , ".ttf", ".fon"
      , ".js", ".ts"
      , ".gif", ".png", ".bmp", ".ico", ".jpeg"
      , ".mp3", ".mp4"
  );
  public static Map<String, String> contentTypeMapping = BUtil.toMap(
    ".html", "text/html"
    , ".css", "text/css"
    , ".js", "application/javascript"
    , ".ico", "image/x-icon"
  );

  public static Result<StaticFile> check(Request request) {
    String fileFullName = fileFullName(request);
    boolean isStaticFile = !(contentTypeMapping.keySet().stream().filter(fileFullName::endsWith)
                                               .findFirst().orElse("").isEmpty());
    String contentType = contentType(request);

    return new Result<>(true, new StaticFile(fileFullName, contentType, isStaticFile));
  }

  public static String fileFullName(Request request) {
    return request.getRequestURI().split("\\?")[0];
  }

  public static String contentType(Request request) {
    return contentTypeMapping.get(fileFullName(request));
  }

  public static class StaticFile {
    public String fileFullName;
    public String contentType;
    public boolean isStaticFile;

    StaticFile(String fileFullName, String contentType, boolean isStaticFile) {
      this.fileFullName = fileFullName;
      this.contentType = contentType;
      this.isStaticFile = isStaticFile;
    }
  }
}

