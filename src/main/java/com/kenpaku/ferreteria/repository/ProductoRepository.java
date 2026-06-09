package com.kenpaku.ferreteria.repository;

import com.kenpaku.ferreteria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
