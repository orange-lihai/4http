package cn.churen.biz.dao.crud;

public class Query<T> {
  private Class<T> clazz;

  public Query(Class<T> clazz) {
    this.clazz = clazz;
  }
}
