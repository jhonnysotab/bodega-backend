package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.ProductoImportadoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FacturaImportService {

  /**
   * Envía la imagen de un voucher/factura a un modelo de visión (Claude) y devuelve
   * la lista de productos que el modelo pudo reconocer.
   */
  List<ProductoImportadoDTO> extraerProductosDeImagen(MultipartFile imagen);
}
