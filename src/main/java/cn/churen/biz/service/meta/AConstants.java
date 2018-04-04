package cn.churen.biz.service.meta;

public class AConstants {
  public enum sql {
    queryPageByName("queryPageByName", ""),
    queryDivById("queryDivById", ""),
    queryDivByParentId("queryDivByParentId", "")
    ;
    public String code;
    public String memo;
    sql(String code, String memo) {
      this.code = code;
      this.memo = memo;
    }
  }
}
