package cn.churen.service;

import cn.churen.dao.crud.Query;
import cn.churen.dao.crud.table.TBSystem;
import cn.churen.http.check.AInjection;
import cn.churen.http.check.AService;

import java.util.List;

@AService
public class IndexService implements IService {

  @AInjection private TableService tableService;

  @ATransactional
  @ALog(beforeLog = "invoke getIndexMessage begin ..."
      , afterLog = "invoke getIndexMessage end ... "
      , logLevel = "SEVERE")
  public List<TBSystem> getIndexMessage() {
    Query<TBSystem> query = new Query<>(TBSystem.class);
    List<TBSystem> list = tableService.queryList(query, TBSystem.class);
    list.size();
    return list;
  }
}
