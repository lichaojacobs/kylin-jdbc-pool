package com.example.demo.config;

import java.util.Map;
import lombok.Data;

/**
 * Created by lichao on 2017/7/15.
 */
@Data
public class MultiKylinSqlProperties {

  private Map<String, KylinSqlProperties> kylin;
}
