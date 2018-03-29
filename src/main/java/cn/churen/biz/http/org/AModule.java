package cn.churen.biz.http.org;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AModule {
  private AModule parentModule;
  private Integer moduleOrder;
}
