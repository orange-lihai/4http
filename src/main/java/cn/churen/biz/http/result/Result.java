package cn.churen.biz.http.result;

public class Result<R> {
  public R data;
  public boolean success;
  public String message;
  public ResultCode resultCode;

  public Result() {
    this.success = true;
    this.resultCode = ResultCode.REQUEST_OK;
  }

  public Result(boolean success, R data) {
    this.success = true;
    this.data = data;
  }

  public Result(boolean success) {
    this.success = success;
    this.resultCode = success ? ResultCode.REQUEST_OK : ResultCode.REQUEST_ERROR;
  }

  public Result(boolean success, ResultCode resultCode, String message) {
    this.success = success;
    this.resultCode = resultCode;
    this.message = message;
  }
}
