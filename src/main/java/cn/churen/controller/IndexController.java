package cn.churen.controller;

import cn.churen.http.check.AController;
import cn.churen.http.check.AControllerMapping;
import cn.churen.http.check.AInjection;
import cn.churen.service.IndexService;

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
