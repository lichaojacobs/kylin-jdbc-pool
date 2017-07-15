package com.example.demo.config;

import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by lichao on 2017/7/4.
 */
public class MapPropertyCondition extends SpringBootCondition {

  @Override
  public ConditionOutcome getMatchOutcome(ConditionContext context,
      AnnotatedTypeMetadata metadata) {
    String prefix = attribute(metadata, "prefix");
    RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(context.getEnvironment());
    Map<String, Object> properties = resolver.getSubProperties(prefix);
    return new ConditionOutcome(!properties.isEmpty(),
        String.format("Map property [%s] is empty", prefix));
  }

  private static String attribute(AnnotatedTypeMetadata metadata, String name) {
    return (String) metadata.getAnnotationAttributes(ConditionalOnMapProperty.class.getName())
        .get(name);
  }
}
