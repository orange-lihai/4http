package cn.churen.biz.http.handler;

import cn.churen.biz.dao.crud.table.meta.TBDiv;
import cn.churen.biz.dao.crud.table.meta.TBPage;
import cn.churen.biz.http.check.CheckStaticFile;
import cn.churen.biz.http.check.MethodInvoke;
import cn.churen.biz.service.TableService;
import cn.churen.biz.service.meta.AConstants;
import cn.churen.biz.service.meta.DivService;
import cn.churen.biz.util.ABasicUtil;
import cn.churen.biz.util.ACollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.Request;

import java.util.HashMap;
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
    String fileFullName = CheckStaticFile.fileFullName(request);
    StringBuilder s = new StringBuilder();
    Map<String, String> parameters = IndexHandler.getParameters(request);
    /*
    String raw = IndexHandler.getRaw(request);
    AQuery query = (new Gson()).fromJson(raw, AQuery.class);
    */
    Map<String, String> pageParam = new HashMap<>();
    pageParam.put("url", fileFullName);
    TBPage p =  MethodInvoke.getServiceClazzMap(TableService.class)
                            .queryOne(AConstants.sql.queryPageByName.code, pageParam, TBPage.class);

    String divId = null != p ? ABasicUtil.getStr(p.getDivId(), "") : "";
    if (StringUtils.isBlank(divId)) {
      s.append("... ...");
    } else {
      // s.append("hello, world!");
      TBDiv div = MethodInvoke.getServiceClazzMap(DivService.class)
                              .queryDivRecursive(divId);
      if (null != div) {
        s.append(div.outerHtml());
      } else {
        s.append("*** ***");
      }
    }
    // wrapperWith(s, HtmlTag.FONT, null);
    wrapperWith(s, HtmlTag.BODY, null);
    s.insert(0, wrapperWith(new StringBuilder(), HtmlTag.HEAD, null));
    wrapperWith(s, HtmlTag.HTML, null);
    return s.toString();
  }
}
