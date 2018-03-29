package cn.churen.biz.http.user;

import cn.churen.biz.http.org.AModule;
import cn.churen.biz.http.org.AOrg;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AUser {
  private String token;
  private String userId;
  private String name;
  private String email;
  private Integer status;
  private String statusDesc;
  private List<AOrg> orgList;
  private List<AModule> modules;
}
