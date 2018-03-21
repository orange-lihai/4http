package cn.churen.biz.service;

import cn.churen.biz.dao.crud.Query;
import cn.churen.biz.dao.crud.table.TBSystem;
import cn.churen.biz.http.check.AInjection;
import cn.churen.biz.http.check.AService;

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
