package com.kenpaku.ferreteria.config;

import com.kenpaku.ferreteria.model.Categoria;
import com.kenpaku.ferreteria.model.Marca;
import com.kenpaku.ferreteria.model.Producto;
import com.kenpaku.ferreteria.model.User;
import com.kenpaku.ferreteria.repository.CategoriaRepository;
import com.kenpaku.ferreteria.repository.MarcaRepository;
import com.kenpaku.ferreteria.repository.ProductoRepository;
import com.kenpaku.ferreteria.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoriaRepository categoriaRepository, MarcaRepository marcaRepository,
                          ProductoRepository productoRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.productoRepository = productoRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initData() {
        initializeUsers();
        initializeProducts();
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("admin"), "admin@kenpaku.com", "ADMIN");
            User user = new User("angelo", passwordEncoder.encode("1234"), "angelo@kenpaku.com", "USER");
            userRepository.save(admin);
            userRepository.save(user);
        }
    }

    private void initializeProducts() {
        if (categoriaRepository.count() == 0 && marcaRepository.count() == 0 && productoRepository.count() == 0) {
            Categoria tornilleria = categoriaRepository.save(new Categoria("Tornillería"));
            Categoria electricidad = categoriaRepository.save(new Categoria("Electricidad"));

            Marca kenpaku = marcaRepository.save(new Marca("Kenpaku"));
            Marca brasilia = marcaRepository.save(new Marca("Brasilia"));

            productoRepository.save(new Producto("KEN-001", "Tornillo 5/16", 1.20, 250, tornilleria, kenpaku));
            productoRepository.save(new Producto("KEN-002", "Cinta aislante", 3.50, 120, electricidad, brasilia));
        }
    }
}
