package com.example.demo;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kylin.jdbc.Driver;

/**
 * Created by lichao on 2017/6/27.
 */
@Slf4j
public class KylinDataSource implements DataSource {

  private int DEFALUT_POOL_SIZE = 10;

  private LinkedList<Connection> connectionPoolList = new LinkedList<>();

  public KylinDataSource(String driver, String url, String userName, String password,
      int poolSize) {
    try {
      Driver driverManager = (Driver) Class.forName(driver).newInstance();
      Properties info = new Properties();
      info.put("user", userName);
      info.put("password", password);
      for (int i = 0; i < (poolSize > 0 ? poolSize : DEFALUT_POOL_SIZE); i++) {
        Connection connection = driverManager
            .connect(url, info);
        connectionPoolList.add(ConnectionProxy.getProxy(connection, connectionPoolList));
      }
      log.info("KylinDataSource has initialized {} size connection pool",
          connectionPoolList.size());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    synchronized (connectionPoolList) {
      if (connectionPoolList.size() == 0) {
        try {
          connectionPoolList.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      return connectionPoolList.removeFirst();
    }
  }


  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return getConnection();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new RuntimeException("Unsupport operation.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return DataSource.class.equals(iface);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    throw new RuntimeException("Unsupport operation.");
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {

  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    throw new RuntimeException("Unsupport operation.");
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return 0;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }


  static class ConnectionProxy implements InvocationHandler {

    private Object obj;
    private LinkedList<Connection> pool;
    private String DEFAULT_CLOSE_METHOD = "close";

    private ConnectionProxy(Object obj, LinkedList<Connection> pool) {
      this.obj = obj;
      this.pool = pool;
    }

    public static Connection getProxy(Object o, LinkedList<Connection> pool) {
      Object proxed = Proxy
          .newProxyInstance(o.getClass().getClassLoader(), new Class[]{Connection.class},
              new ConnectionProxy(o, pool));
      return (Connection) proxed;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getName().equals(DEFAULT_CLOSE_METHOD)) {
        synchronized (pool) {
          pool.add((Connection) proxy);
          pool.notify();
        }
        return null;
      } else {
        return method.invoke(obj, args);
      }
    }
  }
}
