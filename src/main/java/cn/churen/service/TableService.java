package cn.churen.service;

import cn.churen.http.check.AService;

import java.util.ArrayList;
import java.util.List;

@AService
public class TableService {
  
  @ATransactional
  @ALog
  public <T> List<T> queryList(Class<T> clazz) {
    System.out.println(" invoke queryList begin ... ");
    List<T> t = new ArrayList<>();
    System.out.println(" invoke queryList end ... ");
    return t;
  }
}
