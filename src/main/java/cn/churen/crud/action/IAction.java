package cn.churen.crud.action;

public interface IAction<T> {
  public T queryOneByPK(T table, String... keys);
}
