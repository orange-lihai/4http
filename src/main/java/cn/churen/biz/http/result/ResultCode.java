package cn.churen.biz.http.result;

public enum  ResultCode {

  REQUEST_ERROR("RC111110", "请求错误!"),
  REQUEST_OK("RC111111", "请求正确!"),

  HTTP_METHOD_ERROR("RC777770", "HTTP METHOD 错误!"),
  HTTP_METHOD_OK("RC777771", "HTTP METHOD 正确!"),

  AUTH_ERROR("RC888880", "权限 错误!"),
  AUTH_OK("RC888881", "权限 正确!"),

  SYS_INIT("RC000000", ""),
  SYS_ERROR("RC999999", ""),

  METHOD_INVOKE_ERROR("MI0", ""),
  METHOD_INVOKE_OK("MI1", "");

  public String code;
  public String message;

  ResultCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
