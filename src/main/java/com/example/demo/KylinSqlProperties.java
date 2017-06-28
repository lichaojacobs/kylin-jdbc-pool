package com.example.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by lichao on 2017/6/27.
 */
@Data
@ConfigurationProperties("kylin")
public class KylinSqlProperties {

  // 用户名
  private String userName;

  // 密码
  private String password;

  // 是否加密
  private boolean decrypt;

  // 主库地址
  private String connectionUrl;

  private int poolSize;
}
