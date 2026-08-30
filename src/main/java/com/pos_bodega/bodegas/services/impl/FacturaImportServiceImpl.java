package com.pos_bodega.bodegas.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos_bodega.bodegas.controller.dto.ProductoImportadoDTO;
import com.pos_bodega.bodegas.exception.BusinessException;
import com.pos_bodega.bodegas.services.FacturaImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class FacturaImportServiceImpl implements FacturaImportService {

  private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
  private static final String ANTHROPIC_VERSION = "2023-06-01";

  private static final String PROMPT = """
      Eres un asistente que lee fotos de facturas, boletas o vouchers de compra de una bodega/tienda \
      y extrae la lista de productos comprados.

      Responde EXCLUSIVAMENTE con un array JSON (sin markdown, sin texto adicional, sin explicaciones), \
      donde cada elemento tiene esta forma exacta:
      {"nombre": "string", "cantidad": number o null, "precioCompra": number o null}

      Reglas:
      - "nombre" es el nombre del producto tal como aparece en el documento (corrige mayúsculas/typos obvios).
      - "cantidad" es la cantidad comprada de ese producto (entero). Si no se ve claramente, usa null.
      - "precioCompra" es el precio unitario de costo (lo que pagó la bodega por unidad), no el total de la línea. \
      Si no puedes calcularlo con certeza, usa null.
      - Ignora totales, subtotales, impuestos, encabezados y pies de página: solo líneas de producto.
      - Si la imagen no es una factura/voucher o no se distingue ningún producto, responde con un array vacío [].
      """;

  private final ObjectMapper objectMapper;
  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(15))
      .build();

  @Value("${anthropic.api.key:}")
  private String apiKey;

  @Value("${anthropic.api.model:claude-sonnet-5}")
  private String model;

  public FacturaImportServiceImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public List<ProductoImportadoDTO> extraerProductosDeImagen(MultipartFile imagen) {

    if (apiKey == null || apiKey.isBlank()) {
      throw new BusinessException(
          "La importación por imagen aún no está configurada: falta la API key de Anthropic (anthropic.api.key / ANTHROPIC_API_KEY).");
    }
    if (imagen == null || imagen.isEmpty()) {
      throw new BusinessException("Debes adjuntar una imagen de la factura o voucher");
    }

    String mediaType = normalizarMediaType(imagen.getContentType());
    String base64Imagen;
    try {
      base64Imagen = Base64.getEncoder().encodeToString(imagen.getBytes());
    } catch (IOException e) {
      throw new BusinessException("No se pudo leer la imagen enviada");
    }

    Map<String, Object> body = Map.of(
        "model", model,
        "max_tokens", 2048,
        "messages", List.of(Map.of(
            "role", "user",
            "content", List.of(
                Map.of(
                    "type", "image",
                    "source", Map.of(
                        "type", "base64",
                        "media_type", mediaType,
                        "data", base64Imagen)),
                Map.of("type", "text", "text", PROMPT)))));

    String requestJson;
    try {
      requestJson = objectMapper.writeValueAsString(body);
    } catch (IOException e) {
      throw new BusinessException("Error interno preparando la solicitud a Claude");
    }

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(ANTHROPIC_URL))
        .header("x-api-key", apiKey)
        .header("anthropic-version", ANTHROPIC_VERSION)
        .header("content-type", "application/json")
        .timeout(Duration.ofSeconds(60))
        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
        .build();

    HttpResponse<String> response;
    try {
      response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BusinessException("No se pudo conectar con el servicio de reconocimiento de imágenes");
    }

    if (response.statusCode() != 200) {
      throw new BusinessException(
          "Claude respondió con error (" + response.statusCode() + "): " + resumen(response.body()));
    }

    String textoRespuesta = extraerTextoRespuesta(response.body());
    return parsearProductos(textoRespuesta);
  }

  private String normalizarMediaType(String contentType) {
    if (contentType == null) return "image/jpeg";
    return switch (contentType) {
      case "image/png", "image/gif", "image/webp", "image/jpeg" -> contentType;
      default -> "image/jpeg";
    };
  }

  private String extraerTextoRespuesta(String responseBody) {
    try {
      JsonNode root = objectMapper.readTree(responseBody);
      JsonNode content = root.path("content");
      if (content.isArray() && !content.isEmpty()) {
        return content.get(0).path("text").asText("");
      }
      return "";
    } catch (IOException e) {
      throw new BusinessException("No se pudo interpretar la respuesta de Claude");
    }
  }

  private List<ProductoImportadoDTO> parsearProductos(String texto) {
    if (texto == null || texto.isBlank()) {
      return new ArrayList<>();
    }

    // El modelo a veces envuelve el JSON en ```json ... ``` a pesar de la instrucción; lo limpiamos.
    String limpio = texto.trim();
    if (limpio.startsWith("```")) {
      limpio = limpio.replaceFirst("^```[a-zA-Z]*", "").trim();
      if (limpio.endsWith("```")) {
        limpio = limpio.substring(0, limpio.length() - 3).trim();
      }
    }

    try {
      return objectMapper.readValue(limpio, objectMapper.getTypeFactory()
          .constructCollectionType(List.class, ProductoImportadoDTO.class));
    } catch (IOException e) {
      throw new BusinessException("Claude no devolvió un listado de productos válido. Intenta con otra foto.");
    }
  }

  private String resumen(String body) {
    if (body == null) return "";
    return body.length() > 300 ? body.substring(0, 300) + "..." : body;
  }
}
