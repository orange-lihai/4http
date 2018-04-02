package cn.churen.biz.util;

import cn.churen.biz.Application;
import com.google.common.base.Supplier;
import org.glassfish.grizzly.Grizzly;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AContextHolder {
  public enum RC {
    DATA_SOURCE("dataSource"),
    TRANSACTION("Transaction"),
    PARAMETER("parameter"),
    RAW("parameter"),
    MODULE_ID("moduleId");

    public String code;
    RC(String code) { this.code = code; }
  }

  private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();
  private static Logger logger = Grizzly.logger(AContextHolder.class);

	public static void clear() {
		Map<String, Object> tmp = AContextHolder.context.get();
		try {
      if (null != tmp.get(RC.DATA_SOURCE.name())) {
        Connection conn = (Connection) tmp.get(RC.DATA_SOURCE.name());
        conn.close();
      }

      AContextHolder.context.set(null);
      tmp.clear();
    } catch (Exception ex) {}
	}

	public static Map data() {
		return AContextHolder.context.get();
	}


  @SuppressWarnings({"unchecked"})
  public static <T> T get(String key, Class<T> clazz) {
    if (null == AContextHolder.context.get()) {
      AContextHolder.init();
    }
    Object o = AContextHolder.context.get().get(key);
    return (null != o && o.getClass().isAssignableFrom(clazz)) ? (T) o : null;
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T getOrDefault(String key, Class<T> clazz, Object defaultValue) {
    Object rtn = AContextHolder.get(key, clazz);
    return rtn == null ? (T) defaultValue : (T) rtn;
  }

	public static void init() {
		AContextHolder.clear();
		AContextHolder.context.set(new HashMap<>());
	}

	public static void set(Map<String, Object> entries) {
		AContextHolder.context.get().putAll(entries);
	}

	public static void set(String key, Object value) {
		AContextHolder.context.get().put(key, value);
	}

	public static <T> T use(Supplier<T> task, Map<String, Object> replace) {
		try {
			AContextHolder.init();
			AContextHolder.set(replace);
			return task.get();
		} finally {
			AContextHolder.clear();
		}
	}


  // special context objects
  public static Connection getConnection() {
    Connection conn = null;
    try {
      conn = AContextHolder.get(AContextHolder.RC.DATA_SOURCE.name(), Connection.class);
      if (null == conn) {
        conn = Application.dataSource.getConnection();
        conn.setAutoCommit(false);
        AContextHolder.set(AContextHolder.RC.DATA_SOURCE.name(), conn);
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
    return conn;
  }

  public static void rollbackConnection() {
    try {
      Connection conn = getConnection();
      if (null != conn && conn.isClosed()) {
        conn.rollback();
        conn.close();
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
  }
}
