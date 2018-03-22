/**
 * Created by lihai5 on 2017/11/1.
 * js of common page
 */
require(['jquery', 'ajaxUtil', 'baseUtil'], function ($, ajaxUtil, baseUtil) {
  var params = baseUtil.getUrlParameters(window.location.href);
  var moduleId = params['moduleId'] || 0;
  var moduleKey = params['moduleKey'] || "";
  if (moduleId <= 0 && moduleKey === "") { return; }

  var b = 1;
  var rs = ajaxUtil.doGet('/module/config', "a=1&b=2");
  var rs2 = ajaxUtil.doPost('/dfadfs/dfada', "a=1&b=2", {c: 123, d:["a", "b", "c"]});
  var t = new Date();
});
