package cn.churen;

import cn.churen.http.handler.IndexHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;

import java.io.IOException;
import java.net.BindException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
  private static final Logger logger = Grizzly.logger(Application.class);
  public static HikariDataSource dataSource = null;

  private static HttpServer initHttpServer(ResourceBundle props) {
    final int port = Integer.parseInt(props.getString("http.server.port"));
    final boolean jmxEnabled = Boolean.getBoolean(props.getString("http.server.jmxEnabled"));
    final String listenerName = props.getString("http.server.listenerName");

    final HttpServer httpServer = new HttpServer();
    final ServerConfiguration config = httpServer.getServerConfiguration();
    config.setJmxEnabled(jmxEnabled);

    config.addHttpHandler(new IndexHandler(), "/");

    final NetworkListener networkListener = new NetworkListener(
        listenerName,
        NetworkListener.DEFAULT_NETWORK_HOST,
        port
    );
    // networkListener.getFilterChain().add(new AuthFilter());

    // Enable SSL on the listener
    // networkListener.setSecure(true);
    // networkListener.setSSLEngineConfig(createSslConfiguration());

    httpServer.addListener(networkListener);
    return httpServer;
  }

  private static HikariDataSource initDataSource(ResourceBundle prop) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(prop.getString("db.mysql.master.jdbcUrl"));
    config.setUsername(prop.getString("db.mysql.master.userName"));
    config.setPassword(prop.getString("db.mysql.master.password"));
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setAutoCommit(true);
    config.setMaximumPoolSize(Integer.parseInt(prop.getString("db.mysql.master.maxPoolSize")));
    config.setMinimumIdle(Integer.parseInt(prop.getString("db.mysql.master.minimumIdle")));
    return new HikariDataSource(config);
  }

  public static void main(String[] args) {
    // ResourceBundle logProp = ResourceBundle.getBundle("config/application-log");
    ResourceBundle mainProp = ResourceBundle.getBundle("application");
    ResourceBundle dbProp = ResourceBundle.getBundle("config/application-db");
    
    final HttpServer httpServer = initHttpServer(mainProp);
    final NetworkListener networkListener =
        httpServer.getListener(mainProp.getString("http.server.listenerName"));
    
    try {

      dataSource = initDataSource(dbProp);

      httpServer.start();
      logger.log(Level.INFO, "HttpServer running ...");
      logger.log(Level.INFO, "Press any key to stop the server ...");

      Thread.currentThread().join();
    } catch (BindException be) {
      logger.log(Level.SEVERE, "Cannot bind to port {}. Is it already in use?"
          , networkListener.getPort());
      logger.log(Level.SEVERE, be.getMessage(), be);
    } catch (IOException ioe) {
      logger.log(Level.SEVERE, "IO exception while starting server.");
      logger.log(Level.SEVERE, ioe.getMessage(), ioe);
    } catch (InterruptedException ie) {
      logger.log(Level.SEVERE, "Interrupted, shutting down.");
      logger.log(Level.SEVERE, ie.getMessage(), ie);
    } finally {
      httpServer.shutdown();
      if (null != dataSource) { dataSource.close(); }
    }
  }
}
