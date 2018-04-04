-- ----------------------------------------------------------------------------------------------------------------------
select * from tb_system s;
select * from tb_page p;
select * from tb_file f;
select * from tb_div d;
select * from tb_css;
select * from tb_api;
select * from tb_div_css;
select * from tb_div_api;
-- ----------------------------------------------------------------------------------------------------------------------
set @idStr = '';
call pd_childrens('tb_div', 'id', 'pid', 'page_sys_list_div', 1, @idStr);
select find_in_set('page_sys_list_div', @idStr) from dual;
-- ----------------------------------------------------------------------------------------------------------------------

DROP procedure IF EXISTS pd_childrens;
create procedure pd_childrens(in tableName varchar(128), in idName varchar(128), in pidName varchar(128)
  , in id varchar(128), containSelf int
  , inout idStr longtext
) begin
declare lev int; set lev=1;
set @ids = '';

drop table if exists temp_tree;
CREATE TABLE temp_tree(id VARCHAR(128), pid varchar(128), `level` INT);
set @strsql = CONCAT('INSERT temp_tree' ,' SELECT ',idName ,',' ,pidName,',',1, ' FROM ', tableName,' WHERE ',pidName,'=','''', id,'''');
prepare strsql from @strsql;
execute strsql;
while row_count() > 0 do
  set lev=lev+1;
  set @strsql = CONCAT('INSERT temp_tree' ,' SELECT ','t.',idName,',' ,' t.',pidName,',' ,lev, ' FROM ', tableName,' t join temp_tree a on ','t.',pidName,'=a.ID And a.level=', lev-1);
  prepare strsql from @strsql;
  execute strsql;
end while ;

SET @myself='';
IF (containSelf=1) THEN
  SET @myself = concat(' or ',idName,'=','''',id,'''');
ELSE
  SET @myself = '';
END IF;

set @strsql = CONCAT('SELECT group_concat(',idName,') into @ids from ', tableName,' where ',idName,' in ( SELECT ID from temp_tree)',@myself);
prepare strsql from @strsql;
execute strsql;

set idStr = @ids;
end;

-- -----------------------------------------------------------------------------------------------------------------------

-- -----------------------------------------------------------------------------------------------------------------------
-- truncate table tb_system;
-- drop table tb_system;
-- select * from tb_system;
CREATE TABLE `tb_system` (
  `id` varchar(32) NOT NULL,
  `ename` varchar(64) DEFAULT NULL,
  `show_name` varchar(64) DEFAULT NULL,
  `company_name` varchar(32) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL COMMENT 'http://abc.dd.com/system_one/',
  `memo` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='system'

-- truncate table tb_page;
-- drop table tb_page;
-- select * from tb_page;
create table tb_page (
  id varchar(32) primary key comment 'primary key',
  pid varchar(32),
  name varchar(32),
  show_name varchar(128),
  url varchar(128),
  parameter_url varchar(2048),
  memo varchar(1024),
  system_id varchar(32) comment '',
  div_id varchar(32),
  unique (url, system_id)
);

-- truncate table tb_file;
-- drop table tb_file;
-- select * from tb_file;
create table tb_file (
  id varchar(32) primary key comment 'primary key',
  file_type varchar(32) comment 'css, js, gif ...',
  file_url varchar(512),
  is_default int default 0,
  system_id varchar(32) comment '',
  owner varchar(32) comment ''
);

-- truncate table tb_page_file;
-- drop table tb_page_file;
-- select * from tb_page_file;
create table tb_page_file (
  page_id varchar(32),
  file_id varchar(32),
  unique (page_id, file_id)
);

-- truncate table tb_div;
-- drop table tb_div;
-- select * from tb_div;
create table tb_div (
  id varchar(32) primary key comment 'primary key',
  pid varchar(32) comment 'parent id',
  sid varchar(32) comment 'sibling id',
  name varchar(32) comment '',
  show_name varchar(32) comment '',
  data_selector varchar(1024) comment 'examples: name.key1.key2, name[1].key3, ... ',
  html_code text comment 'inner html code',
  css_code longtext comment 'css style code',
  attrs longtext comment 'div attributes',
  system_id varchar(32) comment '',
  div_type varchar(32) comment 'page, div, modal, hide, float ...',
  order_num int default 0
);

-- truncate table tb_div_css;
-- drop table tb_div_css;
-- select * from tb_div_css;
create table tb_div_css (
  div_id varchar(32) primary key comment 'id in tb_div',
  css_id varchar(32) comment 'id in tb_css',
  order_num int default 0
);

-- truncate table tb_div_api;
-- drop table tb_div_api;
-- select * from tb_div_api;
create table tb_div_api (
  div_id varchar(32) primary key comment 'id in tb_div',
  api_id varchar(32) comment 'id in tb_data',
  order_num int default 0
);

-- truncate table tb_css;
-- drop table tb_css;
-- select * from tb_css;
create table tb_css (
  id varchar(32) primary key comment 'primary key',
  name varchar(64) comment 'english name',
  show_name varchar(128) comment '名称',
  css_code longtext comment 'css code',
  owner varchar(32) comment ''
);

-- truncate table tb_api;
-- drop table tb_api;
-- select * from tb_api;
create table tb_api (
  id varchar(32) primary key comment 'primary key',
  name varchar(64) unique comment 'english name',
  show_name varchar(128) comment '名称',
  data_type varchar(8) comment '数据类型: SQL, HTTP, CONSTANTS ... ',
  sql_code longtext comment 'sql code',
  http_statement longtext comment 'http url and parameters',
  constant_data longtext comment 'json',
  owner varchar(32) comment '',
  system_id varchar(32)
);

