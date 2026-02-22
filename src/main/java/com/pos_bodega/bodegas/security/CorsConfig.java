package com.pos_bodega.bodegas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // ⚠️ Orígenes permitidos (Angular dev + prod)
    config.setAllowedOrigins(List.of(
        "http://localhost:4200",
        "http://localhost:4201"
    ));

    // Métodos HTTP permitidos
    config.setAllowedMethods(List.of(
        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));

    // Headers permitidos
    config.setAllowedHeaders(List.of(
        "Authorization",
        "Content-Type",
        "Accept"
    ));

    // Permite enviar cookies/credenciales
    config.setAllowCredentials(true);

    // Cuánto tiempo cachear el preflight (1 hora)
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}