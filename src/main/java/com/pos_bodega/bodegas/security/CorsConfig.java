package com.pos_bodega.bodegas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

  // ⚠️ Orígenes permitidos (Angular dev + prod). En producción se configura con la variable de
  // entorno CORS_ALLOWED_ORIGINS (lista separada por comas), ver docker-compose / DEPLOY.md.
  @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:4201}")
  private String allowedOrigins;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList());

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