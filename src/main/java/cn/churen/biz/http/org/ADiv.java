package cn.churen.biz.http.org;

import cn.churen.biz.http.handler.PageGenerator;
import cn.churen.biz.util.ABasicUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class ADiv {
  public static final String DOM_ID_PREFIX = "div_";

  private String id; // PK
  private Map<String, Object> dataContext;
  private String dataId; // examples: name.key1.key2, name[1].key3, ...
  private String htmlCode;

  private String parentId;
  private ADiv parent;
  private String siblingId;
  private ADiv sibling;
  private List<ADiv> children;
  private List<String> classNames;
  private Map<String, String> cssStyle;
  private Map<String, String> domAttributes;


  // https://www.w3.org/TR/css-flexbox-1/
  private Display display;

  private FlexDirection flexDirection;
  private FlexWrap flexWrap;
  // private String flexFlow; // flex-flow: <flex-direction> || <flex-wrap>;
  private JustifyContent justifyContent;
  private AlignItems alignItems;
  private AlignContent alignContent;

  private Integer order; // order: <integer>;
  private Integer flexGrow; // flex-grow: <number>; /* default 0 */
  private Integer flexShrink; // flex-shrink: <number>; /* default 1 */
  private String  flexBasis; // flex-basis: <length> | auto; /* default auto */
  // private String flex; // flex: none | [ <'flex-grow'> <'flex-shrink'>? || <'flex-basis'> ]
  private AlignSelf alignSelf;

  public enum Display {
    //  display: flex; inline-flex; -webkit-flex; /* Safari */
    flex("flex; -webkit-flex;", "", ""),
    inlineFlex("inline-flex; -webkit-flex;", "", "行内元素也可以使用 Flex 布局");

    String key;
    String code;
    String name;
    String memo;
    Display(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "display";
    }
  }

  public enum FlexDirection {
    // flex-direction: row | row-reverse | column | column-reverse;
    row("row", "水平靠左(默认值)", ""),
    rowReverse("row-reverse", "水平靠右", ""),
    column("column", "垂直向下", ""),
    columnReverse("column-reverse", "垂直向上", "");

    String key;
    String code;
    String name;
    String memo;
    FlexDirection(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "flex-direction";
    }
  }

  public enum FlexWrap {
    // flex-wrap: nowrap | wrap | wrap-reverse;
    nowrap("nowrap", "不换行(默认值)", ""),
    wrap("wrap", "换行,第一行在上方", ""),
    wrapReverse("wrap-reverse", "换行,第一行在下方", "");

    String key;
    String code;
    String name;
    String memo;
    FlexWrap(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "flex-wrap";
    }
  }

  public enum JustifyContent {
    // justify-content: flex-start | flex-end | center | space-between | space-around;
    flexStart("flex-start", "左对齐(默认值)", ""),
    flexEnd("flex-end", "右对齐", ""),
    center("center", "居中", ""),
    spaceBetween("space-between", "两端对齐", "两端对齐,项目之间的间隔都相等."),
    spaceAround("space-around", "两侧间隔相等", "每个项目两侧的间隔相等. 所以, 项目之间的间隔比项目与边框的间隔大一倍");

    String key;
    String code;
    String name;
    String memo;
    JustifyContent(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "justify-content";
    }
  }

  public enum AlignItems {
    // align-items: flex-start | flex-end | center | baseline | stretch;
    flexStart("flex-start", "交叉轴的起点对齐", ""),
    flexEnd("flex-end", "交叉轴的终点对齐", ""),
    center("center", "交叉轴的中点对齐", ""),
    baseline("baseline", "项目的第一行文字的基线对齐", ""),
    stretch("stretch", "(默认值)如果项目未设置高度或设为auto,将占满整个容器的高度.", "");

    String key;
    String code;
    String name;
    String memo;
    AlignItems(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "align-items";
    }
  }

  public enum AlignContent {
    // align-content: flex-start | flex-end | center | space-between | space-around | stretch;
    flexStart("flex-start", "左对齐(默认值)", ""),
    flexEnd("flex-end", "右对齐", ""),
    center("center", "居中", ""),
    spaceBetween("space-between", "两端对齐", "两端对齐,项目之间的间隔都相等."),
    spaceAround("space-around", "两侧间隔相等", "每个项目两侧的间隔相等. 所以, 项目之间的间隔比项目与边框的间隔大一倍"),
    stretch("stretch", "(默认值)轴线占满整个交叉轴", "");

    String key;
    String code;
    String name;
    String memo;
    AlignContent(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "align-content";
    }
  }

  public enum AlignSelf {
    // align-self: auto | flex-start | flex-end | center | baseline | stretch;
    auto("auto", "(默认值)继承父元素", ""),
    flexStart("flex-start", "交叉轴的起点对齐", ""),
    flexEnd("flex-end", "交叉轴的终点对齐", ""),
    center("center", "交叉轴的中点对齐", ""),
    baseline("baseline", "项目的第一行文字的基线对齐", ""),
    stretch("stretch", "(默认值)如果项目未设置高度或设为auto,将占满整个容器的高度.", "");

    String key;
    String code;
    String name;
    String memo;
    AlignSelf(String code, String name, String memo) {
      this.code = code;
      this.name = name;
      this.memo = memo;
      this.key = "align-self";
    }
  }

  // ----------------------------------------------------------------------------------------- //
  public ADiv() {}
  public ADiv(String id) { this.id = id; }
  // ----------------------------------------------------------------------------------------- //
  public void appendChildren(ADiv... divs) {
    if (null == children) { children = new ArrayList<>(); }
    children.addAll(Arrays.asList(divs));
  }

  public void appendCssStyle(Map<String, String> css) {
    if (null == cssStyle) { cssStyle = new HashMap<>(); }
    cssStyle.putAll(css);
  }

  public void appendCssStyle(String k, String v) {
    if (null == cssStyle) { cssStyle = new HashMap<>(); }
    cssStyle.put(k, v);
  }

  public void appendClassNames(String... z) {
    if (null == classNames) { classNames = new ArrayList<>(); }
    classNames.addAll(Arrays.asList(z));
  }

  public void appendDomAttributes(HashMap<String, String> attrs) {
    if (null == domAttributes) { domAttributes = new HashMap<>(); }
    domAttributes.putAll(attrs);
  }

  public void appendDomAttributes(String k, String v) {
    if (null == domAttributes) { domAttributes = new HashMap<>(); }
    domAttributes.put(k, v);
  }


  public StringBuilder html(StringBuilder innerHtml) {
    StringBuilder s = new StringBuilder(
        ABasicUtil.getStr(ABasicUtil.getStr(innerHtml, htmlCode), DOM_ID_PREFIX + "_" + id)
    );
    domAttributes = (null == domAttributes) ? new HashMap<>() : domAttributes;
    domAttributes.put("id", DOM_ID_PREFIX + "_" + id);
    if (CollectionUtils.isNotEmpty(classNames)) {
      domAttributes.compute("class", (key, oldValue) -> {
        String[] oldClassNames = StringUtils.split(oldValue, " ");
        for (int z = 0; null != oldClassNames && z < oldClassNames.length; z++) {
          classNames.add(oldClassNames[z]);
        }
        return StringUtils.join(classNames, " ");
      });
    }
    cssStyle = (null == cssStyle) ? new HashMap<>() : cssStyle;
    if (null != display) { cssStyle.put(display.key, display.code); }
    if (null != flexDirection) { cssStyle.put(flexDirection.key, flexDirection.code); }
    if (null != flexWrap) { cssStyle.put(flexWrap.key, flexWrap.code); }
    if (null != justifyContent) { cssStyle.put(justifyContent.key, justifyContent.code); }
    if (null != alignItems) { cssStyle.put(alignItems.key, alignItems.code); }
    if (null != alignContent) { cssStyle.put(alignContent.key, alignContent.code); }
    if (null != order) { cssStyle.put("order", order.toString()); }
    if (null != flexGrow) { cssStyle.put("flex-grow", flexGrow.toString()); }
    if (null != flexShrink) { cssStyle.put("flex-shrink", flexShrink.toString()); }
    if (null != flexBasis) { cssStyle.put("flex-basis", flexBasis); }
    if (null != alignSelf) { cssStyle.put(alignSelf.key, alignSelf.code); }

    domAttributes.compute("style", (key, oldValue) -> {
      List<String> st = cssStyle.keySet().stream().map(m -> " " + m + ": " + cssStyle.get(m) + ";")
                                .collect(Collectors.toList());
      String newValue = StringUtils.join(st, " ") + " " + ABasicUtil.getStr(oldValue, "");
      newValue = newValue.replaceAll(";{2,}", ";").trim();
      return newValue;
    });

    PageGenerator.wrapperWith(s, PageGenerator.HtmlTag.DIV, domAttributes);
    return s;
  }

  public StringBuilder outerHtml() {
    StringBuilder s = new StringBuilder();
    if (CollectionUtils.isEmpty(children)) {
      s.append(html(new StringBuilder(ABasicUtil.getStr(htmlCode, DOM_ID_PREFIX + id))));
    } else {
      for (int i = 0; null != children && i < children.size(); i++) {
        s.append(children.get(i).outerHtml());
      }
      s = html(s);
    }
    return s;
  }
  // ----------------------------------------------------------------------------------------- //

}
