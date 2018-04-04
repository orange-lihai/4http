package cn.churen.biz.dao.crud.table.meta;

import cn.churen.biz.dao.crud.table.ATable;
import cn.churen.biz.dao.crud.table.ATableColumn;
import cn.churen.biz.dao.crud.table.ITable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ATable(name = "tb_page")
public class TBPage implements ITable {
  private String id;
  private String pid;
  private String name;
  @ATableColumn(name = "show_name") private String showName;
  private String url;
  private String memo;
  @ATableColumn(name = "system_id") private String systemId;
  @ATableColumn(name = "div_id") private String divId;
}
