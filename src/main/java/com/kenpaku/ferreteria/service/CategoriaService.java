package com.kenpaku.ferreteria.service;

import com.kenpaku.ferreteria.dto.CategoriaDTO;
import com.kenpaku.ferreteria.exception.ResourceNotFoundException;
import com.kenpaku.ferreteria.model.Categoria;
import com.kenpaku.ferreteria.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDTO> obtenerTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return convertToDTO(categoria);
    }

    public CategoriaDTO crear(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria(categoriaDTO.getNombre());
        categoria.setActivo(categoriaDTO.getActivo() != null ? categoriaDTO.getActivo() : true);
        Categoria guardada = categoriaRepository.save(categoria);
        return convertToDTO(guardada);
    }

    public CategoriaDTO actualizar(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setActivo(categoriaDTO.getActivo());

        Categoria actualizada = categoriaRepository.save(categoria);
        return convertToDTO(actualizada);
    }

    public void desactivar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    public void activar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        categoria.setActivo(true);
        categoriaRepository.save(categoria);
    }

    public long contar() {
        return categoriaRepository.count();
    }

    private CategoriaDTO convertToDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNombre(), categoria.isActivo());
    }
}
