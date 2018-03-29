package cn.churen.biz.http.org;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AOrg {
  private AOrg parentOrg;
  private Integer orgOrder;
}
