package com.example.demo.mapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lichao on 2017/6/27.
 */
public class MapperPlugin {

  private static final Function<Object, String> bytes2UTF8String =
      bytes -> bytes instanceof String ? bytes.toString() :
          new String((byte[]) bytes, Charset.forName("UTF-8"));

  private static final Function<PropertyDescriptor, Class> pd2Generic =
      pd -> getCollectionGeneric(pd.getReadMethod());


  private final Predicate<PropertyDescriptor> predicate;
  private final ColumnValue columnValue;

  private MapperPlugin(Predicate<PropertyDescriptor> predicate,
      ColumnValue columnValue) {
    this.predicate = predicate;
    this.columnValue = columnValue;
  }

  boolean test(PropertyDescriptor pd) {
    return predicate.test(pd);
  }

  Object getColumnValue(Object object, PropertyDescriptor pd) {
    return columnValue.get(object, pd);
  }

  public static MapperPluginsBuilder of(Predicate<PropertyDescriptor> predicate) {
    return new MapperPluginsBuilder(predicate);
  }

  public static MapperPluginsBuilder ofNot(Predicate<PropertyDescriptor> predicate) {
    return of(predicate.negate());
  }

  public static MapperPluginsBuilder of(Class clazz) {
    return of(pd -> clazz.isAssignableFrom(pd.getPropertyType()));
  }

  @FunctionalInterface
  public interface ColumnValue {

    Object get(Object object, PropertyDescriptor pd);
  }

  public static class MapperPluginsBuilder {

    Predicate<PropertyDescriptor> predicate;

    public MapperPluginsBuilder(Predicate<PropertyDescriptor> predicate) {
      this.predicate = predicate;
    }

    public MapperPlugin columnValue(ColumnValue columnValue) {
      return new MapperPlugin(predicate, columnValue);
    }
  }

  static final MapperPlugin JsonPlugin =
      MapperPlugin.ofNot(pd -> pd.getPropertyType().isPrimitive() ||
          Primitives.isWrapperType(pd.getPropertyType()) ||
          String.class.isAssignableFrom(pd.getPropertyType()) ||
          Date.class.isAssignableFrom(pd.getPropertyType()))
          .columnValue((object, pd) ->
              Optional.ofNullable(object)
                  .map(bytes2UTF8String)
                  .map(json -> JSON.parseObject(json, pd.getPropertyType()))
                  .orElse(null));

  static final MapperPlugin JSONObjectPlugin =
      MapperPlugin.of(JSONObject.class)
          .columnValue((object, pd) ->
              Optional.ofNullable(object)
                  .map(bytes2UTF8String)
                  .map(JSONObject::parseObject)
                  .orElse(new JSONObject()));

  static final MapperPlugin ListPlugin =
      MapperPlugin.of(List.class)
          .columnValue((object, pd) ->
              Optional.ofNullable(object)
                  .map(bytes2UTF8String)
                  .map(json -> JSON.parseArray(json, pd2Generic.apply(pd)))
                  .orElse(new ArrayList<>()));

  static final MapperPlugin SetPlugin =
      MapperPlugin.of(Set.class)
          .columnValue((object, pd) ->
              Optional.ofNullable(object)
                  .map(bytes2UTF8String)
                  .map(json -> JSON.parseArray(json, pd2Generic.apply(pd)))
                  .map(list -> Sets.newHashSet(List.class.cast(list)))
                  .orElse(new HashSet<>()));

  static final MapperPlugin MapPlugin =
      MapperPlugin.of(Map.class)
          .columnValue((object, pd) ->
              Optional.ofNullable(object)
                  .map(bytes2UTF8String)
                  .map(json -> JSONObject.parseObject(json, Map.class))
                  .orElse(new HashMap<>()));

  static final MapperPlugin EnumPlugin =
      MapperPlugin.of(Enum.class)
          .columnValue((o, pd) -> {
            try {
              if (o == null) {
                return null;
              }
              if (o instanceof Number) {
                Number number = (Number) o;
                Method method = pd.getPropertyType()
                    .getMethod("valueByIndex", Integer.TYPE);
                return method.invoke(null, number.intValue());
              } else {
                String val = o.toString();
                Method method = pd.getPropertyType().getMethod("fromString", String.class);
                return method.invoke(null, val);
              }
            } catch (NoSuchMethodException e) {
              throw new RuntimeException(
                  "getColumnValue error, NoSuchMethod : valueByIndex or fromString", e);
            } catch (InvocationTargetException e) {
              throw new RuntimeException(
                  "getColumnValue error, InvocationTargetException ", e);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(
                  "getColumnValue error, IllegalAccessException ", e);
            }
          });

  private static Class<?> getCollectionGeneric(Method method) {
    if (Collection.class.isAssignableFrom(method.getReturnType())) {
      Type fc = method.getGenericReturnType();
      if (fc == null) {
        return Object.class;
      }
      if (fc instanceof ParameterizedType) {
        ParameterizedType pt = (ParameterizedType) fc;
        return (Class) pt.getActualTypeArguments()[0];
      }
      return Object.class;
    }
    return Object.class;
  }

}
