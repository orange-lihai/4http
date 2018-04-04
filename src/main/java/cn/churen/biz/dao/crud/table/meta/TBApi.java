package cn.churen.biz.dao.crud.table.meta;

import cn.churen.biz.dao.crud.table.ATable;
import cn.churen.biz.dao.crud.table.ATableColumn;
import cn.churen.biz.dao.crud.table.ITable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ATable(name = "tb_api")
public class TBApi implements ITable {
  private String id;
  private String name;
  @ATableColumn(name = "show_name") private String showName;
  @ATableColumn(name = "data_type") private String dataType;
  @ATableColumn(name = "sql_code") private String sqlCode;
  @ATableColumn(name = "http_statement") private String httpStatement;
  @ATableColumn(name = "constant_data") private String constantData;
  @ATableColumn(name = "owner") private String owner;
}
