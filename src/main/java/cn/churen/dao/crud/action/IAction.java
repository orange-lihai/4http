package cn.churen.dao.crud.action;

public interface IAction<T> {
  public T queryOneByPK(T table, String... keys);
}
