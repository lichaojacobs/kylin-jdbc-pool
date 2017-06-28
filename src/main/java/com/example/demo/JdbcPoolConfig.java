package com.example.demo;

import lombok.Data;

@Data
public class JdbcPoolConfig {

  public static final String DEFAULT_DRIVER_CLASS_NAME = "org.apache.kylin.jdbc.Driver";

  public static final int DEFAULT_INITIAL_SIZE = 10;

  public static final int DEFAULT_MIN_IDLE = 1;

  public static final int DEFAULT_MAX_IDLE = 5;

  public static final int DEFAULT_MAX_ACTIVE = 10;

  public static final int DEFAULT_MAX_WAIT = 10000;

  public static final int DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60000;

  public static final int DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 300000;

  public static final String DEFAULT_VALIDATION_QUERY = "SELECT 'x';";

  public static final boolean DEFAULT_TEST_WHILE_IDLE = true;

  public static final boolean DEFAULT_TEST_ON_BORROW = false;

  public static final boolean DEFAULT_TEST_ON_RETURN = false;

  // Driver Class Name
  private String driverClassName = DEFAULT_DRIVER_CLASS_NAME;

  // 初始数量
  private int initialSize = DEFAULT_INITIAL_SIZE;

  // 最小空闲数
  private int minIdle = DEFAULT_MIN_IDLE;

  // 最大空闲数
  private int maxIdle = DEFAULT_MAX_IDLE;

  // 最大连接数
  private int maxActive = DEFAULT_MAX_ACTIVE;

  // 最大等待时间
  private int maxWait = DEFAULT_MAX_WAIT;

  // 空闲连接回收的间隔时间
  private int timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

  // 空闲连接被回收的时间
  private int minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

  // 用于检查连接的SQL
  private String validationQuery = DEFAULT_VALIDATION_QUERY;

  // 是否开启空闲连接校验
  private boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;

  // 借出连接是否校验
  private boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;

  // 返回连接是否校验
  private boolean testOnReturn = DEFAULT_TEST_ON_RETURN;

}
