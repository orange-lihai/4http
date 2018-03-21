package cn.churen.biz.dao.crud.table;

@ATable(name = "tb_system")
public class TBSystem implements ITable {
  private String id;
  private String ename;
  @ATableColumn(name = "show_name") private String showName;
  @ATableColumn(name = "company_name") private String companyName;
  private String url;
  private String memo;

  /* setters and getters begin **/
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEname() {
    return ename;
  }

  public void setEname(String ename) {
    this.ename = ename;
  }

  public String getShowName() {
    return showName;
  }

  public void setShowName(String showName) {
    this.showName = showName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  /* setters and getters end **/
}
