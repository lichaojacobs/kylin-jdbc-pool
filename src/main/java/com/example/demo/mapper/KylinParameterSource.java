package com.example.demo.mapper;

import static com.example.demo.mapper.SourcePlugin.EnumPlugin;
import static com.example.demo.mapper.SourcePlugin.JsonPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

/**
 * Created by lichao on 2017/6/27.
 */
public class KylinParameterSource extends BeanPropertySqlParameterSource {

  private List<SourcePlugin> sourcePlugins;

  private KylinParameterSource(Object object, List<SourcePlugin> sourcePlugins) {
    super(object);
    this.sourcePlugins = sourcePlugins;
  }

  @Override
  public Object getValue(String paramName) throws IllegalArgumentException {
    Object value = super.getValue(paramName);
    if (Objects.isNull(value)) {
      return null;
    }
    return sourcePlugins.stream()
        .filter(sourcePlugin -> sourcePlugin.test(value))
        .map(sourcePlugin -> sourcePlugin.getValue(value))
        .findFirst().orElse(value);
  }

  @Override
  public int getSqlType(String paramName) {
    Object value = super.getValue(paramName);
    if (Objects.isNull(value)) {
      return super.getSqlType(paramName);
    }
    return sourcePlugins.stream()
        .filter(sourcePlugin -> sourcePlugin.test(value))
        .map(sourcePlugin -> sourcePlugin.getSqlType(value))
        .findFirst().orElse(super.getSqlType(paramName));
  }

  public static KylinParameterSource getDefault(Object object) {
    return KylinParameterSource.builder().value(object)
        .sourcePlugins(EnumPlugin)
        .sourcePlugins(JsonPlugin)
        .build();
  }

  public static KylinParameterSource withDefault(Object object, SourcePlugin... sourcePlugins) {
    KylinParameterSource.KylinParameterSourceBuilder builder =
        KylinParameterSource.builder().value(object);
    for (final SourcePlugin sourcePlugin : sourcePlugins) {
      builder.sourcePlugins(sourcePlugin);
    }
    return builder
        .sourcePlugins(EnumPlugin)
        .sourcePlugins(JsonPlugin)
        .build();
  }

  public static KylinParameterSource.KylinParameterSourceBuilder builder() {
    return new KylinParameterSource.KylinParameterSourceBuilder();
  }

  public static class KylinParameterSourceBuilder {

    private Object object;
    private ArrayList<SourcePlugin> sourcePlugins;

    KylinParameterSourceBuilder() {
    }

    public KylinParameterSource.KylinParameterSourceBuilder value(Object object) {
      this.object = object;
      return this;
    }

    public KylinParameterSource.KylinParameterSourceBuilder sourcePlugins(
        SourcePlugin sourcePlugin) {
      if (this.sourcePlugins == null) {
        this.sourcePlugins = new ArrayList();
      }
      this.sourcePlugins.add(sourcePlugin);
      return this;
    }

    public KylinParameterSource build() {
      List<SourcePlugin> sourcePlugins;
      switch (this.sourcePlugins == null ? 0 : this.sourcePlugins.size()) {
        case 0:
          sourcePlugins = Collections.emptyList();
          break;
        case 1:
          sourcePlugins = Collections.singletonList(this.sourcePlugins.get(0));
          break;
        default:
          sourcePlugins = Collections.unmodifiableList(new ArrayList<>(this.sourcePlugins));
      }
      return new KylinParameterSource(this.object, sourcePlugins);
    }

    public String toString() {
      return "RhllorParameterSource.RhllorParameterSourceBuilder(object=" + this.object
          + ", sourcePlugins=" + this.sourcePlugins + ")";
    }
  }
}
