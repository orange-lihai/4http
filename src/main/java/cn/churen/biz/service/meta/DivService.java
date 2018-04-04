package cn.churen.biz.service.meta;

import cn.churen.biz.dao.crud.table.meta.TBDiv;
import cn.churen.biz.http.check.AService;
import cn.churen.biz.http.check.MethodInvoke;
import cn.churen.biz.service.TableService;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@AService
public class DivService {
  
  public TBDiv queryDivRecursive(String divId) {
    TBDiv d = queryDiv(divId);
    if (null == d) { return d; }
    Map<Integer, List<TBDiv>> divLevelMap = new HashMap<>();
    divLevelMap.put(0, Collections.singletonList(d));
    for (int i = 1; i < 100; i++) {
      List<TBDiv> preLevel = divLevelMap.get(i - 1);
      List<TBDiv> curLevel = new ArrayList<>();
      if (CollectionUtils.isEmpty(preLevel)) {
        break;
      } else {
        for (TBDiv p : preLevel) {
          List<TBDiv> ch = MethodInvoke.getServiceClazzMap(TableService.class)
                                       .queryList(AConstants.sql.queryDivByParentId.code,
                                                  Collections.singletonMap("pid", p.getId()),
                                                  TBDiv.class
                                       );
          if (CollectionUtils.isNotEmpty(ch)) {
            curLevel.addAll(ch);
          }
        }
        divLevelMap.put(i, curLevel);
      }
    }
    int maxLevel = divLevelMap.keySet().stream().max(Integer::compareTo).orElse(0);
    for (int i = 0; i < maxLevel; i++) {
      List<TBDiv> nextLevel = divLevelMap.get(i + 1);
      List<TBDiv> curLevel = divLevelMap.get(i);
      for (int j = 0; j < curLevel.size(); j++) {
        TBDiv c = curLevel.get(j);
        if (j < curLevel.size() - 1) {
          c.setSibling(curLevel.get(j + 1));
          c.setSiblingId(c.getSibling().getId());
        }
        if (null != nextLevel) {
          List<TBDiv> _children = nextLevel.stream().filter(m -> Objects.equals(m.getParentId(), c.getId()))
                                           .collect(Collectors.toList());
          c.appendChildren(_children);
        }
        tidy(c);
      }
    }
    return d;
  }

  public TBDiv queryDiv(String divId) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", divId);
    TBDiv d = MethodInvoke.getServiceClazzMap(TableService.class)
                          .queryOne(AConstants.sql.queryDivById.code, params, TBDiv.class);
    if (null != d) { tidy(d); }
    return d;
  }

  private void tidy(TBDiv d) {

  }

  public String outerHtml() {
    return null;
  }
}
