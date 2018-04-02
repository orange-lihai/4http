package cn.churen.biz.http.org;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AModule {
  private AModule parent;
  private Integer order;
  private List<AModule> childes;

  private String id;
  private String name;
  private String showName;
  private String memo;

  private ADiv div;
}
