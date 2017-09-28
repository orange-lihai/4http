package cn.churen.http.result;

public enum  ResultCode {

  REQUEST_ERROR("RC111110"),
  REQUEST_OK("RC111111"),

  HTTP_METHOD_ERROR("RC777770"),
  HTTP_METHOD_OK("RC777771"),

  AUTH_ERROR("RC888880"),
  AUTH_OK("RC888881"),

  SYS_INIT("RC000000"),
  SYS_ERROR("RC999999"),

  METHOD_INVOKE_ERROR("MI0"),
  METHOD_INVOKE_OK("MI1");

  public String code;

  ResultCode(String code) {
      this.code = code;
    }
}
