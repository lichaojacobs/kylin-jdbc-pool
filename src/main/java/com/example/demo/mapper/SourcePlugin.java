package com.example.demo.mapper;

import com.alibaba.fastjson.JSON;
import com.google.common.primitives.Primitives;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lichao on 2017/6/27.
 */
public class SourcePlugin {

  private final Predicate<Object> predicate;
  private final Function<Object, Object> function;
  private final Function<Object, Integer> supplier;

  public boolean test(Object value) {
    return predicate.test(value);
  }

  public Object getValue(Object value) {
    return function.apply(value);
  }

  public Integer getSqlType(Object value) {
    return supplier.apply(value);
  }

  private SourcePlugin(Predicate<Object> predicate,
      Function<Object, Object> function,
      Function<Object, Integer> supplier) {
    this.predicate = predicate;
    this.function = function;
    this.supplier = supplier;
  }

  public static SourcePluginsBuilder of(Predicate<Object> predicate) {
    return new SourcePluginsBuilder(predicate);
  }

  public static SourcePluginsBuilder ofNot(Predicate<Object> predicate) {
    return of(predicate.negate());
  }

  public static SourcePluginsBuilder of(Class clazz) {
    return of(object -> Objects.nonNull(object) && clazz.isAssignableFrom(object.getClass()));
  }

  public static class SourcePluginsBuilder {

    Predicate<Object> predicate;
    Function<Object, Integer> supplier;

    public SourcePluginsBuilder(Predicate<Object> predicate) {
      this.predicate = predicate;
    }

    public SourcePluginsBuilder sqlType(Function<Object, Integer> supplier) {
      this.supplier = supplier;
      return this;
    }

    public SourcePlugin getValue(Function<Object, Object> function) {
      return new SourcePlugin(predicate, function, supplier);
    }
  }

  static final SourcePlugin EnumPlugin =
      SourcePlugin
          .of(Enum.class)
          .sqlType(value -> Arrays.stream(value.getClass().getMethods())
              .map(Method::getName).anyMatch(
                  Predicate.isEqual("valueByIndex")) ? Types.TINYINT : Types.VARCHAR)
          .getValue(value -> {
            try {
              Method method = null;
              if (Arrays.stream(value.getClass().getMethods())
                  .map(Method::getName).anyMatch(Predicate.isEqual("valueByIndex"))) {
                method = value.getClass().getMethod("getIndex");
              } else {
                method = value.getClass().getMethod("getName");
              }
              return method.invoke(value, null);
            } catch (IllegalAccessException e) {
              throw new RuntimeException("getColumnValue error, IllegalAccessException ", e);
            } catch (InvocationTargetException e) {
              throw new RuntimeException("getColumnValue error, InvocationTargetException ", e);
            } catch (NoSuchMethodException e) {
              throw new RuntimeException(
                  "getColumnValue error, NoSuchMethod : getIndex or getName", e);
            }
          });


  static final SourcePlugin JsonPlugin =
      SourcePlugin.ofNot(value -> value.getClass().isPrimitive() ||
          Primitives.isWrapperType(value.getClass()) ||
          String.class.isAssignableFrom(value.getClass()) ||
          Date.class.isAssignableFrom(value.getClass()))
          .sqlType(o -> Types.VARBINARY)
          .getValue(value -> JSON.toJSONString(value).getBytes());


}
