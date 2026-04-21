package com.cotizador.danos.shared.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeansConfig {

  @Bean
  Clock systemClock() {
    return Clock.systemUTC();
  }
}
