-- -----------------------------------------------------------------------------------------------------------------------
--       select * from tb_system s;
--       select * from tb_page p;
--       select * from tb_file f;
--       select * from tb_page_file;
--       select * from tb_div d;
--       select * from tb_css;
--       select * from tb_api;
--       select * from tb_div_css;
--       select * from tb_div_api;
-- -----------------------------------------------------------------------------------------------------------------------
set @sysId = '0';
delete from tb_system where id = @sysId;
insert into tb_system(id, ename, show_name, company_name, url, memo)
values (@sysId, 'meta-system', 'meta-system', 'www.churen.biz', 'http://localhost:9999/meta-system/', 'meta-system');
-- ---------------------------------------------------------------------------------------------------------------------------

set @pageSysList = 'page_sys_list';
set @pageSysGenerator = 'page_sys_generator';
delete from tb_page where system_id in (@sysId);
insert into tb_page(id, pid, name, show_name, url, parameter_url, memo, system_id)
values (@pageSysList, null, 'page_sys_list', 'page of system list', '/system/list.html', '', '', @sysId)
, (@pageSysGenerator, null, 'page_sys_generator', 'page of system generator', '/system/generator.html', 'systemId=#{systemId}', '', @sysId)
;
-- ---------------------------------------------------------------------------------------------------------------------------

set @divId = 'page_sys_list_div';
set @idStr = '';
call pd_childrens('tb_div', 'id', 'pid', @divId, 1, @idStr);
delete from tb_div where find_in_set(id, @idStr) = 1;

insert into tb_div(id, pid, sid, name, show_name, data_selector, html_code, css_code, attrs, system_id, div_type)
values (@divId, null, null, 'page_sys_list_div', 'page_sys_list_div', null, null, null, null, @sysId, 'div')
;
update tb_page set div_id = @divId where system_id = @sysId and id = @pageSysList;

-- ---------------------------------------------------------------------------------------------------------------------------
set @divIdG = 'page_sys_generator_div';
set @idStrG = '';
call pd_childrens('tb_div', 'id', 'pid', @divIdG, 1, @idStrG);
select @idStrG from dual;
delete from tb_div where find_in_set(id, @idStrG);

insert into tb_div(id, pid, sid, name, show_name, data_selector, html_code, css_code, attrs, system_id, div_type)
values (@divIdG, null, null, 'page_sys_generator_div', 'page_sys_generator_div', '', '', '', '', @sysId, 'div')
;
update tb_page set div_id = @divIdG where system_id = @sysId and id = @pageSysGenerator;
update tb_div set css_code = concat(css_code, 'flex-direction: column;') where id = @divIdG;
update tb_div set css_code = concat(css_code, 'height: 100%;') where id = @divIdG;

insert into tb_div(id, pid, sid, name, show_name, data_selector, html_code, css_code, attrs, system_id, div_type)
values ('psgd_1', @divIdG, null, 'psgd_1', 'psgd_1', '', '', '', '', @sysId, 'div')
, ('psgd_2', @divIdG, null, 'psgd_2', 'psgd_2', '', '', '', '', @sysId, 'div')
, ('psgd_3', @divIdG, null, 'psgd_3', 'psgd_3', '', '', '', '', @sysId, 'div')
;
update tb_div set css_code = concat(css_code, 'flex-grow: 1;') where id = 'psgd_2';
update tb_div set css_code = concat(css_code, 'flex-shrink: 0;') where id = 'psgd_2';
update tb_div set css_code = concat(css_code, 'flex-basis: auto;') where id = 'psgd_2';

-- ---------------------------------------------------------------------------------------------------------------------------
update tb_div set css_code = concat(css_code, 'display: flex;');
update tb_div set css_code = concat(css_code, 'display: -webkit-flex;');
-- ---------------------------------------------------------------------------------------------------------------------------

-- select * from tb_api;
delete from tb_api where owner = 'sys' and name in ('queryPageByName', 'queryDivById', 'queryDivByParentId');
insert into tb_api(id, name, data_type, sql_code, owner) values
(REPLACE(UUID(),'-',''), 'queryPageByName', 'SQL', 'select p.* from tb_page p where p.url like concat('''', #{url}, ''%'')', 'sys'),
(REPLACE(UUID(),'-',''), 'queryDivById', 'SQL', 'select d.* from tb_div d where d.id = #{id}', 'sys'),
(REPLACE(UUID(),'-',''), 'queryDivByParentId', 'SQL', 'select d.* from tb_div d where d.pid = #{pid} order by d.order_num asc', 'sys')
;

-- ---------------------------------------------------------------------------------------------------------------------------
commit;

/*
show full processlist;

select d.* from tb_div d where d.pid = 'page_sys_list_div' order by d.order_num asc;
select * from tb_api a where a.data_type = 'SQL' and a.owner = 'sys' and a.name = 'queryDivByParentId'

select * from tb_api a where a.sql_code = 'SQL' and a.owner = 'sys' and a.name = 'queryPageByName';
select d.* from tb_div d where d.id = 'page_sys_list_div';

*/
