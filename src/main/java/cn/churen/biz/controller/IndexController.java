package cn.churen.biz.controller;

import cn.churen.biz.http.check.AController;
import cn.churen.biz.http.check.AControllerMapping;
import cn.churen.biz.http.check.AInjection;
import cn.churen.biz.service.IndexService;

import java.util.HashMap;

@AController
public class IndexController implements IController {

  @AInjection private IndexService indexService;

  @AControllerMapping(uri = "/")
  public Object index(HashMap<String, String> params
    , Integer id
  ) {
    return indexService.getIndexMessage();
  }
}
