package cn.churen.util;

import cn.churen.Application;
import com.google.common.base.Supplier;
import org.glassfish.grizzly.Grizzly;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextHolder {
  public enum RC {
    DATA_SOURCE("dataSource"),
    TRANSACTION("Transaction")
    ;
    RC(String dataSource) {}
  }
  private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();
  private static Logger logger = Grizzly.logger(ContextHolder.class);

	public static void clear() {
		Map<String, Object> tmp = ContextHolder.context.get();
		try {
      if (null != tmp.get(RC.DATA_SOURCE.name())) {
        Connection conn = (Connection) tmp.get(RC.DATA_SOURCE.name());
        conn.close();
      }

      ContextHolder.context.set(null);
      tmp.clear();
    } catch (Exception ex) {}
	}

	public static Map data() {
		return ContextHolder.context.get();
	}


  @SuppressWarnings({"unchecked"})
  public static <T> T get(String key, Class<T> clazz) {
    if (null == ContextHolder.context.get()) {
      ContextHolder.init();
    }
    Object o = ContextHolder.context.get().get(key);
    return (null != o && o.getClass().isAssignableFrom(clazz)) ? (T) o : null;
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T getOrDefault(String key, Class<T> clazz, Object defaultValue) {
    Object rtn = ContextHolder.get(key, clazz);
    return rtn == null ? (T) defaultValue : (T) rtn;
  }

	public static void init() {
		ContextHolder.clear();
		ContextHolder.context.set(new HashMap<>());
	}

	public static void set(Map<String, Object> entries) {
		ContextHolder.context.get().putAll(entries);
	}

	public static void set(String key, Object value) {
		ContextHolder.context.get().put(key, value);
	}

	public static <T> T use(Supplier<T> task, Map<String, Object> replace) {
		try {
			ContextHolder.init();
			ContextHolder.set(replace);
			return task.get();
		} finally {
			ContextHolder.clear();
		}
	}


  // special context objects
  public static Connection getConnection() throws SQLException {
    Connection conn = null;
    try {
      conn = ContextHolder.get(ContextHolder.RC.DATA_SOURCE.name(), Connection.class);
      if (null == conn) {
        conn = Application.dataSource.getConnection();
        conn.setAutoCommit(false);
        ContextHolder.set(ContextHolder.RC.DATA_SOURCE.name(), conn);
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
    return conn;
  }

  public static void rollbackConnection() {
    try {
      Connection conn = getConnection();
      if (null != conn && !conn.isClosed()) {
        conn.rollback();
        conn.close();
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
  }
}
