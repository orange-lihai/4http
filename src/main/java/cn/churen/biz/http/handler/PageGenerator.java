package cn.churen.biz.http.handler;

import cn.churen.biz.http.check.MethodInvoke;
import cn.churen.biz.http.org.AModule;
import cn.churen.biz.service.ModuleService;
import cn.churen.biz.util.ACollectionUtil;
import cn.churen.biz.util.AContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.Request;

import java.util.Map;

public class PageGenerator {

  public enum HtmlTag {
    HTML("html"),
    BODY("body"),
    HEAD("head"),
    DIV("div"),
    TABLE("table"),
    TR("tr"),
    TD("td"),
    UL("ul"),
    LI("li"),
    A("a"),
    SPAN("span"),
    FONT("font"),
    IMG("img")
    ;

    String name;
    HtmlTag(String name) { this.name = name; }
  }

  public static StringBuilder wrapperWith(StringBuilder s, HtmlTag tag, Map<String, String> attrs) {
    StringBuilder sbAttrs = new StringBuilder();
    if (null != attrs) {
      for (String k : attrs.keySet()) {
        if (ACollectionUtil.isNoneEmpty(k, attrs.get(k))) {
          sbAttrs.append(" ")
                 .append(k)
                 .append("=")
                 .append("\"")
                 .append(attrs.get(k))
                 .append("\"");
        }
      }
    }
    s.insert(0, "<"+tag.name+ sbAttrs.toString()+">");
    s.append("</"+tag.name+">");
    return s;
  }

  public static String generatePage(Request request) {
    StringBuilder s = new StringBuilder();
    Map<String, String> parameters = IndexHandler.getParameters(request);
    /*
    String raw = IndexHandler.getRaw(request);
    AQuery query = (new Gson()).fromJson(raw, AQuery.class);
    */
    String moduleId = parameters.get(AContextHolder.RC.MODULE_ID.code);
    if (StringUtils.isBlank(moduleId)) {
      s.append("... ...");
    } else {
      // s.append("hello, world!");
      AModule m = MethodInvoke.getInstance().getServiceClazzMap(ModuleService.class).getAdminAModule(moduleId);
      s.append(m.getDiv().outerHtml());
    }
    // wrapperWith(s, HtmlTag.FONT, null);
    wrapperWith(s, HtmlTag.BODY, null);
    s.insert(0, wrapperWith(new StringBuilder(), HtmlTag.HEAD, null));
    wrapperWith(s, HtmlTag.HTML, null);
    return s.toString();
  }
}
