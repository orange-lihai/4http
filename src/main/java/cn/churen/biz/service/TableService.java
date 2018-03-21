package cn.churen.biz.service;

import cn.churen.biz.dao.TableDao;
import cn.churen.biz.dao.crud.Query;
import cn.churen.biz.dao.crud.table.TBSystem;
import cn.churen.biz.http.check.AInjection;
import cn.churen.biz.http.check.AService;

import java.util.List;

@AService
public class TableService {
  @AInjection private TableDao tableDao;

  @ATransactional
  @ALog(beforeLog = "invoke queryList begin ...", afterLog = "invoke queryList end ...")
  public <T> List<T> queryList(Query<TBSystem> query, Class<T> clazz) {
    String _sql = "select * from tb_system";
    return tableDao.queryList(_sql, clazz);
  }
}
