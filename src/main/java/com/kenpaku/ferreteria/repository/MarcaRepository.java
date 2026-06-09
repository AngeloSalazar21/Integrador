package com.kenpaku.ferreteria.repository;

import com.kenpaku.ferreteria.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
}
