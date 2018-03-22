define(['jquery', 'apiConfig'], function ($, apiConfig){
  // exports
  return {
    getUrlParameters: function (_url) {
      var url = decodeURI(_url).replace('#', '');
      var rv = {};
      var str = url.substring(url.indexOf('?') + 1, url.length);
      var args = str.split("&");
      for(var i = 0; i < args.length; i++)　　 {
        var pair = args[i].split("=");
        if(pair.length <= 1) continue;
        rv[pair[0]] = pair[1];
      }
      return rv;
    }
  };
});
