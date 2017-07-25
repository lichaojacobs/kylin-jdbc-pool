package com.example.demo.mapper;

import static com.example.demo.mapper.MapperPlugin.EnumPlugin;
import static com.example.demo.mapper.MapperPlugin.JSONObjectPlugin;
import static com.example.demo.mapper.MapperPlugin.JsonPlugin;
import static com.example.demo.mapper.MapperPlugin.ListPlugin;
import static com.example.demo.mapper.MapperPlugin.MapPlugin;
import static com.example.demo.mapper.MapperPlugin.SetPlugin;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lichao on 2017/6/27.
 */
public class KylinRowMapper<T> extends CommonBeanPropertyRowMapper<T> {

  private List<MapperPlugin> mapperPlugins;

  private KylinRowMapper(Class<T> tClass, List<MapperPlugin> mapperPlugins) throws Exception {
    super(tClass);
    this.mapperPlugins = mapperPlugins;
  }

  @Override
  protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd)
      throws SQLException {
    Object object = rs.getObject(index);
    return mapperPlugins.stream()
        .filter(mapperPlugin -> mapperPlugin.test(pd))
        .map(mapperPlugin -> mapperPlugin.getColumnValue(object, pd))
        .findFirst()
        .orElse(super.getColumnValue(rs, index, pd));
  }

  public static <T> KylinRowMapper<T> getDefault(Class<T> tClass) {
    return KylinRowMapper.<T>builder().tClass(tClass)
        .mapperPlugins(JSONObjectPlugin)
        .mapperPlugins(ListPlugin)
        .mapperPlugins(SetPlugin)
        .mapperPlugins(MapPlugin)
        .mapperPlugins(EnumPlugin)
        .mapperPlugins(JsonPlugin)
        .build();
  }

  public static <T> KylinRowMapper<T> withDefault(Class<T> tClass, MapperPlugin... mapperPlugins) {
    RhllorRowMapperBuilder<T> builder = KylinRowMapper.<T>builder().tClass(tClass);
    for (final MapperPlugin mapperPlugin : mapperPlugins) {
      builder.mapperPlugins(mapperPlugin);
    }
    return builder
        .mapperPlugins(JSONObjectPlugin)
        .mapperPlugins(ListPlugin)
        .mapperPlugins(SetPlugin)
        .mapperPlugins(MapPlugin)
        .mapperPlugins(EnumPlugin)
        .mapperPlugins(JsonPlugin)
        .build();
  }

  public static <T> KylinRowMapper.RhllorRowMapperBuilder<T> builder() {
    return new KylinRowMapper.RhllorRowMapperBuilder<>();
  }

  public static class RhllorRowMapperBuilder<T> {

    private Class<T> tClass;
    private ArrayList<MapperPlugin> mapperPlugins;

    RhllorRowMapperBuilder() {
    }

    public KylinRowMapper.RhllorRowMapperBuilder<T> tClass(Class<T> tClass) {
      this.tClass = tClass;
      return this;
    }

    public KylinRowMapper.RhllorRowMapperBuilder<T> mapperPlugins(MapperPlugin mapperPlugin) {
      if (this.mapperPlugins == null) {
        this.mapperPlugins = new ArrayList();
      }
      this.mapperPlugins.add(mapperPlugin);
      return this;
    }

    public KylinRowMapper<T> build() {
      List<MapperPlugin> mapperPlugins;
      switch (this.mapperPlugins == null ? 0 : this.mapperPlugins.size()) {
        case 0:
          mapperPlugins = Collections.emptyList();
          break;
        case 1:
          mapperPlugins = Collections.singletonList(this.mapperPlugins.get(0));
          break;
        default:
          mapperPlugins = Collections.unmodifiableList(new ArrayList<>(this.mapperPlugins));
      }
      try {
        return new KylinRowMapper<>(this.tClass, mapperPlugins);
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      return null;
    }

    public String toString() {
      return "KylinRowMapper.KylinRowMapperBuilder(tClass=" + this.tClass + ", mapperPlugins="
          + this.mapperPlugins + ")";
    }
  }


}
