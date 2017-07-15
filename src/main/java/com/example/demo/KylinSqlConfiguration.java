package com.example.demo;

import com.example.demo.config.ConditionalOnMapProperty;
import com.example.demo.config.KylinSqlProperties;
import com.example.demo.config.MultiKylinSqlProperties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by lichao on 2017/6/27.
 */
@Slf4j
@Configuration
@ConditionalOnMapProperty(prefix = "kylin.")
public class KylinSqlConfiguration implements BeanFactoryPostProcessor, EnvironmentAware {

  private ConfigurableEnvironment environment;

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory beanFactory) throws BeansException {
    MultiKylinSqlProperties multiKylinSqlProperties = resolverSetting("",
        MultiKylinSqlProperties.class);
    multiKylinSqlProperties.getKylin()
        .forEach((name, properties) -> createDataSourceBean(beanFactory, name, properties));
  }

  public void createDataSourceBean(ConfigurableListableBeanFactory beanFactory,
      String prefixName,
      KylinSqlProperties sqlProperties) {

    DataSource baseDataSource = new KylinDataSource(
        JdbcPoolConfig.DEFAULT_DRIVER_CLASS_NAME,
        sqlProperties.getConnectionUrl(),
        sqlProperties.getUserName(),
        sqlProperties.getPassword(),
        sqlProperties.getPoolSize() > 0 ? sqlProperties.getPoolSize()
            : JdbcPoolConfig.DEFAULT_INITIAL_SIZE);

    register(beanFactory, new JdbcTemplate(baseDataSource), prefixName + "JdbcTemplateFactory",
        prefixName);
  }

  private void register(ConfigurableListableBeanFactory beanFactory, Object bean, String name,
      String alias) {
    beanFactory.registerSingleton(name, bean);
    if (!beanFactory.containsSingleton(alias)) {
      beanFactory.registerAlias(name, alias);
    }
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }

  private <T> T resolverSetting(String targetName, Class<T> clazz) {
    PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>(clazz);
    factory.setTargetName(targetName);
    factory.setPropertySources(environment.getPropertySources());
    factory.setConversionService(environment.getConversionService());
    try {
      factory.bindPropertiesToTarget();
      return (T) factory.getObject();
    } catch (Exception e) {
      log.error("Could not bind DataSourceSettings properties: " + e.getMessage(), e);
      throw new FatalBeanException("Could not bind DataSourceSettings properties", e);
    }
  }
}
