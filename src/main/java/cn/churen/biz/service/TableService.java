package cn.churen.biz.service;

import cn.churen.biz.dao.TableDao;
import cn.churen.biz.dao.crud.Query;
import cn.churen.biz.dao.crud.table.meta.TBApi;
import cn.churen.biz.dao.crud.table.meta.TBSystem;
import cn.churen.biz.http.check.AInjection;
import cn.churen.biz.http.check.AService;
import cn.churen.biz.util.ABasicUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AService
public class TableService {
  @AInjection private TableDao tableDao;

  @ATransactional
  @ALog(beforeLog = "invoke queryList begin ...", afterLog = "invoke queryList end ...")
  public <T> List<T> queryList(Query<TBSystem> query, Class<T> clazz) {
    String _sql = "select * from tb_system";
    return tableDao.queryList(_sql, clazz);
  }

  public <T> List<T> queryList(String sqlName, Map<String, ?> params, Class<T> clazz) {
    String _metaSql = "select * from tb_api a where a.data_type = 'SQL' and a.owner = 'sys'"
                  + " and a.name = '" + sqlName + "'" ;
    TBApi a = tableDao.queryOne(_metaSql, TBApi.class);

    if (null != a && StringUtils.isNotBlank(a.getSqlCode())) {
      String _sql = a.getSqlCode();
      _sql = bindingParameters(_sql, params);
      return tableDao.queryList(_sql, clazz);
    } else {
      return null;
    }
  }

  public <T> T queryOne(String sqlName, Map<String, ?> params, Class<T> clazz) {
    List<T> r = queryList(sqlName, params, clazz);
    return CollectionUtils.isEmpty(r) ? null : r.get(0);
  }

  // --------------------------------------------------------------------------------------------
  private String bindingParameters(String sql, Map<String, ?> params) {
    if (null == params) { return sql; }
    for (String k : params.keySet()) {
      String k0 = "#\\{" + k + "}";
      Object v = params.get(k);
      String v0 = v.toString();
      if (v instanceof CharSequence) {
        v0 = "'" + v0 + "'";
      } else if (v instanceof Date) {
        v0 = "DATE_FORMAT('" + ABasicUtil.getDateTimeStr((Date) v) + "', '%Y-%m-%d %H:%i:%s')";
      } else {
        v0 = v.toString();
      }
      sql = sql.replaceAll(k0, v0);
    }
    return sql;
  }
  // --------------------------------------------------------------------------------------------
}
