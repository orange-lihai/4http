package cn.churen.biz.http.result;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
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

  public Result(boolean success, ResultCode resultCode) {
    this.success = success;
    this.resultCode = resultCode;
  }

  // ------------------------------------------------------------------------------------ //
  public interface ASupplier<T> {
    T get() throws Throwable;
  }

  public static <R> Result<R> build(ASupplier<R> supplier) {
    Result<R> r = new Result<>(false, ResultCode.SYS_INIT);
    try {
      r.data = supplier.get();
      r = new Result<>(true, ResultCode.REQUEST_OK, "success");
    } catch (Throwable throwable) {
      r = new Result<>(true, ResultCode.REQUEST_ERROR, throwable.getMessage());
      log.error(throwable.getMessage(), throwable);
    }
    return r;
  }
  // ------------------------------------------------------------------------------------ //
}
