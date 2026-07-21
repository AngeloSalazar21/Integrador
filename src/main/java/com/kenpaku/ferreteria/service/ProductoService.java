package com.kenpaku.ferreteria.service;

import com.kenpaku.ferreteria.dto.ProductoDTO;
import com.kenpaku.ferreteria.exception.ResourceNotFoundException;
import com.kenpaku.ferreteria.model.Categoria;
import com.kenpaku.ferreteria.model.Marca;
import com.kenpaku.ferreteria.model.Producto;
import com.kenpaku.ferreteria.repository.CategoriaRepository;
import com.kenpaku.ferreteria.repository.MarcaRepository;
import com.kenpaku.ferreteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                          MarcaRepository marcaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
    }

    public List<ProductoDTO> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> obtenerPorFiltros(Long categoriaId, Long marcaId, Boolean activo) {
        return productoRepository.findAll()
                .stream()
                .filter(p -> categoriaId == null || p.getCategoria().getId().equals(categoriaId))
                .filter(p -> marcaId == null || p.getMarca().getId().equals(marcaId))
                .filter(p -> activo == null || p.isActivo() == activo)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return convertToDTO(producto);
    }

    public ProductoDTO crear(ProductoDTO productoDTO) {
        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Marca marca = marcaRepository.findById(productoDTO.getMarcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));

        String sku = generateSku();

        Producto producto = new Producto(
                sku,
                productoDTO.getNombre(),
                productoDTO.getPrecio(),
                productoDTO.getStock(),
                categoria,
                marca
        );
        producto.setActivo(Boolean.TRUE.equals(productoDTO.getActivo()));

        Producto guardado = productoRepository.save(producto);
        return convertToDTO(guardado);
    }

    private String generateSku() {
        long count = productoRepository.count() + 1;
        return String.format("PROD-%05d", count);
    }

    public ProductoDTO actualizar(Long id, ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Marca marca = marcaRepository.findById(productoDTO.getMarcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));

        // El SKU no se edita desde el formulario; se conserva el existente.
        if (productoDTO.getSku() != null && !productoDTO.getSku().isBlank()) {
            producto.setSku(productoDTO.getSku());
        }
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setActivo(productoDTO.getActivo());

        Producto actualizado = productoRepository.save(producto);
        return convertToDTO(actualizado);
    }

    public void desactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public void activar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        producto.setActivo(true);
        productoRepository.save(producto);
    }

    public long contar() {
        return productoRepository.count();
    }

    public long contarPorCategoria(Long categoriaId) {
        return productoRepository.findAll()
                .stream()
                .filter(p -> p.getCategoria().getId().equals(categoriaId))
                .count();
    }

    private ProductoDTO convertToDTO(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria().getId(),
                producto.getMarca().getId(),
                producto.getCategoria().getNombre(),
                producto.getMarca().getNombre(),
                producto.isActivo()
        );
    }
}
