package com.example.demo.config;

import java.util.Map;
import lombok.Data;

/**
 * @author lichao
 * @date 2017/11/12
 */
@Data
public class MultiKylinSqlProperties {

  private Map<String, KylinSqlProperties> kylin;
}
