package cn.churen.service;

import cn.churen.http.check.AInjection;
import cn.churen.http.check.AService;

import java.sql.Connection;
import java.util.List;

@AService
public class IndexService implements IService {

  @AInjection private TableService tableService;

  @ATransactional
  @ALog
  public String getIndexMessage(Connection conn) {
    System.out.println(" invoke getIndexMessage begin ... ");
    List<String> r = tableService.queryList(String.class);
    String t = "index message " + r.size();
    System.out.println(" invoke getIndexMessage end ... ");
    return t;
  }
}
