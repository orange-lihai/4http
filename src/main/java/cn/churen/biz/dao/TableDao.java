package cn.churen.biz.dao;

import cn.churen.biz.http.check.AService;
import cn.churen.biz.service.ADao;
import cn.churen.biz.service.AResultType;
import cn.churen.biz.service.ASql;
import lombok.extern.slf4j.Slf4j;

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
}
