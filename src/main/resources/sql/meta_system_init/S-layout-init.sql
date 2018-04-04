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
insert into tb_page(id, pid, name, show_name, url, memo, system_id)
values (@pageSysList, null, 'page_sys_list', 'page of system list', '/system/list.html', '', @sysId)
, (@pageSysGenerator, null, 'page_sys_generator', 'page of system generator', '/system/generator.html?systemId=#{systemId}', '', @sysId)
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
delete from tb_div where find_in_set(id, @idStrG) = 1;

insert into tb_div(id, pid, sid, name, show_name, data_selector, html_code, css_code, attrs, system_id, div_type)
values (@divIdG, null, null, 'page_sys_generator_div', 'page_sys_generator_div', null, null, null, null, @sysId, 'div')
;
update tb_page set div_id = @divIdG where system_id = @sysId and id = @pageSysGenerator;

-- ---------------------------------------------------------------------------------------------------------------------------
commit;
