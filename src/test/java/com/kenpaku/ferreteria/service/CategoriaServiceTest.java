package com.kenpaku.ferreteria.service;

import com.kenpaku.ferreteria.dto.CategoriaDTO;
import com.kenpaku.ferreteria.exception.ResourceNotFoundException;
import com.kenpaku.ferreteria.model.Categoria;
import com.kenpaku.ferreteria.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Pruebas unitarias de {@link CategoriaService}.
 */
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("crear() persiste la categoría con estado activo por defecto")
    void crearCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Pinturas");
        dto.setActivo(null);

        CategoriaDTO creada = categoriaService.crear(dto);

        assertThat(creada.getId()).isEqualTo(1L);
        assertThat(creada.getNombre()).isEqualTo("Pinturas");
        assertThat(creada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("obtenerTodas() convierte las entidades a DTO")
    void obtenerTodas() {
        Categoria c1 = new Categoria("A");
        c1.setId(1L);
        Categoria c2 = new Categoria("B");
        c2.setId(2L);
        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoriaDTO> lista = categoriaService.obtenerTodas();

        assertThat(lista).extracting(CategoriaDTO::getNombre).containsExactly("A", "B");
    }

    @Test
    @DisplayName("actualizar() sobre id inexistente lanza ResourceNotFoundException")
    void actualizarInexistente() {
        when(categoriaRepository.findById(7L)).thenReturn(Optional.empty());

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("X");

        assertThatThrownBy(() -> categoriaService.actualizar(7L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
