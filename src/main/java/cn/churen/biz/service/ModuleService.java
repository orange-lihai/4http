package cn.churen.biz.service;

import cn.churen.biz.http.check.AService;
import cn.churen.biz.http.org.ADiv;
import cn.churen.biz.http.org.AModule;

@AService
public class ModuleService {
  public AModule getAdminAModule(String moduleId) {
    AModule r = new AModule();
    r.setId(moduleId);
    r.setName("rootModule");
    r.setShowName("rootModule");
    r.setMemo("rootModule");

    ADiv rootDiv = new ADiv("0");
    rootDiv.setDisplay(ADiv.Display.flex);
    rootDiv.setFlexDirection(ADiv.FlexDirection.column);
    rootDiv.appendCssStyle("height", "100%");

    ADiv headerDiv = new ADiv("1");
    headerDiv.setDisplay(ADiv.Display.flex);
    headerDiv.setJustifyContent(ADiv.JustifyContent.flexStart);
    ADiv header11 = new ADiv("11");
    ADiv header12 = new ADiv("12");
    ADiv header13 = new ADiv("13");
    headerDiv.appendChildren(header11, header12, header13);

    ADiv contentDiv = new ADiv("2");
    contentDiv.setDisplay(ADiv.Display.flex);
    contentDiv.appendCssStyle("flex", "1 0 auto;");
    contentDiv.setJustifyContent(ADiv.JustifyContent.flexStart);
    ADiv content21 = new ADiv("21");
    content21.setFlexGrow(2);
    ADiv content22 = new ADiv("22");
    content22.setFlexGrow(10);
    contentDiv.appendChildren(content21, content22);

    ADiv footerDiv = new ADiv("3");
    footerDiv.setDisplay(ADiv.Display.flex);
    footerDiv.setJustifyContent(ADiv.JustifyContent.flexEnd);

    rootDiv.appendChildren(headerDiv, contentDiv, footerDiv);
    r.setDiv(rootDiv);
    return r;
  }
}
