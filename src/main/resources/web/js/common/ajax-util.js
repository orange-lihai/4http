define(['jquery', 'apiConfig'], function ($, apiConfig){
  // model of Result -------------------------------------------------------------------------------
  function Result(_options) {
    _options = _options || {};
    this.success = (undefined !== _options.success) ? _options.success : false;
    this.data = (undefined !== _options.data) ? _options.data : {};
    this.message = (undefined !== _options.message) ? _options.message : "";

    this.setters = function (_attrs) {
      $.extend(this, _attrs);
    }
  }
  // model of Result END----------------------------------------------------------------------------

  var doGet = function (_url, _urlParams) {
    var rs = new Result({success: false});
    $.ajax({
      type: 'get',
      url: apiConfig.apiBaseUrl + ((_urlParams) ? ("?" + _urlParams) : ""),
      async: false,
      success: function(res) {
        rs.setters({data: res['data'], message: res['message']});
      },
      error:function (XMLHttpRequest, textStatus, errorThrown){
        rs.setters({data: textStatus, message: errorThrown});
        console.log(errorThrown);
      }
    });
    return rs;
  };
  
  var doPost = function (url, _urlParams, _bodyData) {
    var rs = new Result({success: false});
    $.ajax({
      type: 'post',
      url: apiConfig.apiBaseUrl + ((_urlParams) ? ("?" + _urlParams) : ""),
      data: JSON.stringify(_bodyData),
      contentType: 'application/json; charset=utf-8',
      dataType: 'json',
      async: false,
      success: function(res) {
        rs.setters({data: res['data'], message: res['message']});
      },
      error:function (XMLHttpRequest, textStatus, errorThrown){
        rs.setters({data: textStatus, message: errorThrown});
        console.log(errorThrown);
      }
    });
    return rs;
  };
  // exports
  return {
    doGet: doGet,
    doPost: doPost
  };
});
