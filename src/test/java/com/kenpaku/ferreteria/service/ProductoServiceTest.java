package com.kenpaku.ferreteria.service;

import com.kenpaku.ferreteria.dto.ProductoDTO;
import com.kenpaku.ferreteria.exception.ResourceNotFoundException;
import com.kenpaku.ferreteria.model.Categoria;
import com.kenpaku.ferreteria.model.Marca;
import com.kenpaku.ferreteria.model.Producto;
import com.kenpaku.ferreteria.repository.CategoriaRepository;
import com.kenpaku.ferreteria.repository.MarcaRepository;
import com.kenpaku.ferreteria.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link ProductoService} usando Mockito (sin contexto Spring).
 * Verifican la lógica de negocio de forma aislada de la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoria;
    private Marca marca;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Tornillería");
        categoria.setId(1L);
        marca = new Marca("Kenpaku");
        marca.setId(2L);
    }

    @Test
    @DisplayName("crear() genera un SKU automático con formato PROD-#####")
    void crearGeneraSkuAutomatico() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(marcaRepository.findById(2L)).thenReturn(Optional.of(marca));
        when(productoRepository.count()).thenReturn(4L);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoDTO nuevo = new ProductoDTO();
        nuevo.setNombre("Martillo");
        nuevo.setPrecio(15.0);
        nuevo.setStock(10);
        nuevo.setCategoriaId(1L);
        nuevo.setMarcaId(2L);
        nuevo.setActivo(true);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        productoService.crear(nuevo);
        org.mockito.Mockito.verify(productoRepository).save(captor.capture());

        assertThat(captor.getValue().getSku()).isEqualTo("PROD-00005");
        assertThat(captor.getValue().getNombre()).isEqualTo("Martillo");
        assertThat(captor.getValue().isActivo()).isTrue();
    }

    @Test
    @DisplayName("crear() falla si la categoría no existe")
    void crearFallaSinCategoria() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        ProductoDTO dto = new ProductoDTO();
        dto.setCategoriaId(99L);
        dto.setMarcaId(2L);

        assertThatThrownBy(() -> productoService.crear(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrada");
    }

    @Test
    @DisplayName("obtenerPorId() lanza excepción si el producto no existe")
    void obtenerPorIdInexistente() {
        when(productoRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    @DisplayName("desactivar() marca el producto como inactivo")
    void desactivarProducto() {
        Producto producto = new Producto("PROD-00001", "Clavo", 0.5, 100, categoria, marca);
        producto.setActivo(true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        productoService.desactivar(1L);

        assertThat(producto.isActivo()).isFalse();
    }

    @Test
    @DisplayName("obtenerPorFiltros() filtra por categoría")
    void filtrarPorCategoria() {
        Categoria otra = new Categoria("Electricidad");
        otra.setId(9L);
        Producto p1 = new Producto("PROD-00001", "Tornillo", 1.0, 10, categoria, marca);
        Producto p2 = new Producto("PROD-00002", "Cable", 2.0, 5, otra, marca);
        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductoDTO> resultado = productoService.obtenerPorFiltros(1L, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Tornillo");
    }
}
