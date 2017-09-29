package cn.churen.dao;

import cn.churen.http.check.AService;
import cn.churen.service.ADao;
import cn.churen.service.AResultType;
import cn.churen.service.ASql;
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
