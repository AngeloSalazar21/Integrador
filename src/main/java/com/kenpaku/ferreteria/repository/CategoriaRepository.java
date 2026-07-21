package com.kenpaku.ferreteria.repository;

import com.kenpaku.ferreteria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
