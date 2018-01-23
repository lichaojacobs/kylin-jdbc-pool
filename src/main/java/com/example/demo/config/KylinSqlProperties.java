package com.example.demo.config;

import lombok.Data;

/**
 * @author lichao
 * @date 2017/11/12
 */
@Data
public class KylinSqlProperties {

  private static final String DEFAULT_DRIVER_CLASS_NAME = "org.apache.kylin.jdbc.Driver";
  private static final int DEFAULT_POOL_SIZE = 10;
  private static final Long DEFAULT_MAX_WAIT_TIME = 10000L;

  /**
   * 用户名
   */
  private String userName;

  /**
   * 密码
   */
  private String password;

  /**
   * 是否加密
   */
  private boolean decrypt;

  /**
   * 主库连接地址
   */
  private String connectionUrl;

  /**
   * 最长等待连接时间
   */
  private long maxWaitTime = DEFAULT_MAX_WAIT_TIME;

  private int poolSize = DEFAULT_POOL_SIZE;

  private String driverClassName = DEFAULT_DRIVER_CLASS_NAME;
}
