package com.cotizador.danos.shared.config;

import java.lang.reflect.Constructor;
import org.springframework.stereotype.Component;

@Component
public class DomainReflectionMapper {

  public <T> T instantiate(Class<T> type, Class<?>[] parameterTypes, Object... args) {
    try {
      Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
      constructor.setAccessible(true);
      return constructor.newInstance(args);
    } catch (ReflectiveOperationException exception) {
      throw new IllegalStateException("Unable to instantiate domain type: " + type.getName(), exception);
    }
  }
}
