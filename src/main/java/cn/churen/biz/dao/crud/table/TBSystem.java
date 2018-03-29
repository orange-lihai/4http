package cn.churen.biz.dao.crud.table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ATable(name = "tb_system")
public class TBSystem implements ITable {
  private String id;
  private String ename;
  @ATableColumn(name = "show_name") private String showName;
  @ATableColumn(name = "company_name") private String companyName;
  private String url;
  private String memo;
}
