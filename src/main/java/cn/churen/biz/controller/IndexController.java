package cn.churen.biz.controller;

import cn.churen.biz.http.check.AController;
import cn.churen.biz.http.check.AControllerMapping;
import cn.churen.biz.http.check.AInjection;
import cn.churen.biz.service.IndexService;
import cn.churen.biz.service.ModuleService;
import cn.churen.biz.service.TableService;

import java.util.HashMap;

@AController
public class IndexController implements IController {
  @AInjection private ModuleService moduleService;
  @AInjection private IndexService indexService;
  @AInjection private TableService tableService;

  @AControllerMapping(uri = "/")
  public Object index(HashMap<String, String> params
    , Integer id
  ) {
    return indexService.getIndexMessage();
  }
}
