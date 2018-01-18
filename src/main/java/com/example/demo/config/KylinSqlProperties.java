package com.example.demo.config;

import lombok.Data;

/**
 * Created by lichao on 2017/6/27.
 */
@Data
public class KylinSqlProperties {

  private static final String DEFAULT_DRIVER_CLASS_NAME = "org.apache.kylin.jdbc.Driver";
  private static final int DEFAULT_POOL_SIZE = 10;

  // 用户名
  private String userName;

  // 密码
  private String password;

  // 是否加密
  private boolean decrypt;

  // 主库地址
  private String connectionUrl;

  private int poolSize = DEFAULT_POOL_SIZE;

  private String driverClassName = DEFAULT_DRIVER_CLASS_NAME;
}
