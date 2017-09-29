package cn.churen.service;

import cn.churen.dao.TableDao;
import cn.churen.dao.crud.Query;
import cn.churen.dao.crud.table.TBSystem;
import cn.churen.http.check.AInjection;
import cn.churen.http.check.AService;

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
