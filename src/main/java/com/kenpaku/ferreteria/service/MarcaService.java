package com.kenpaku.ferreteria.service;

import com.kenpaku.ferreteria.dto.MarcaDTO;
import com.kenpaku.ferreteria.exception.ResourceNotFoundException;
import com.kenpaku.ferreteria.model.Marca;
import com.kenpaku.ferreteria.repository.MarcaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarcaService {
    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public List<MarcaDTO> obtenerTodas() {
        return marcaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MarcaDTO obtenerPorId(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        return convertToDTO(marca);
    }

    public MarcaDTO crear(MarcaDTO marcaDTO) {
        Marca marca = new Marca(marcaDTO.getNombre());
        marca.setActivo(marcaDTO.getActivo() != null ? marcaDTO.getActivo() : true);
        Marca guardada = marcaRepository.save(marca);
        return convertToDTO(guardada);
    }

    public MarcaDTO actualizar(Long id, MarcaDTO marcaDTO) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));

        marca.setNombre(marcaDTO.getNombre());
        marca.setActivo(marcaDTO.getActivo());

        Marca actualizada = marcaRepository.save(marca);
        return convertToDTO(actualizada);
    }

    public void desactivar(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        marca.setActivo(false);
        marcaRepository.save(marca);
    }

    public void activar(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        marca.setActivo(true);
        marcaRepository.save(marca);
    }

    public long contar() {
        return marcaRepository.count();
    }

    private MarcaDTO convertToDTO(Marca marca) {
        return new MarcaDTO(marca.getId(), marca.getNombre(), marca.isActivo());
    }
}
