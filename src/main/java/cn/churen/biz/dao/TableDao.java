package cn.churen.biz.dao;

import cn.churen.biz.http.check.AService;
import cn.churen.biz.service.ADao;
import cn.churen.biz.service.AResultType;
import cn.churen.biz.service.ASql;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@AService
@Slf4j
public class TableDao {
  @ADao
  public <T> List<T> queryList(@ASql String sql, @AResultType Class<T> clazz) {
    log.debug(sql);
    return new ArrayList<>();
  }

  public <T> T queryOne(String sql, Class<T> clazz) {
    log.debug(sql);
    List<T> list = queryList(sql, clazz);
    if (CollectionUtils.isNotEmpty(list) && list.size() > 0) {
      return list.get(0);
    } else {
      return null;
    }
  }
}
