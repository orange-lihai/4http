package cn.churen.biz.dao;

import cn.churen.biz.http.check.AService;
import cn.churen.biz.service.ADao;
import cn.churen.biz.service.AResultType;
import cn.churen.biz.service.ASql;
import org.glassfish.grizzly.Grizzly;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@AService
public class TableDao {
  private Logger logger = Grizzly.logger(TableDao.class);

  @ADao
  public <T> List<T> queryList(@ASql String sql, @AResultType Class<T> clazz) {
    logger.log(Level.INFO, sql);
    return new ArrayList<>();
  }
}
