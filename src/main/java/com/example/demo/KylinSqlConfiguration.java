package com.example.demo;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by lichao on 2017/6/27.
 */
@Configuration
@EnableConfigurationProperties(KylinSqlProperties.class)
public class KylinSqlConfiguration {

  @Autowired
  KylinSqlProperties sqlProperties;

  @Bean("kylinJdbcTemplate")
  public JdbcTemplate getDataSource() {
    DataSource baseDataSource = new KylinDataSource(
        JdbcPoolConfig.DEFAULT_DRIVER_CLASS_NAME,
        sqlProperties.getConnectionUrl(),
        sqlProperties.getUserName(),
        sqlProperties.getPassword(),
        sqlProperties.getPoolSize() > 0 ? sqlProperties.getPoolSize()
            : JdbcPoolConfig.DEFAULT_INITIAL_SIZE);

    return new JdbcTemplate(baseDataSource);
  }
}
