package com.kenpaku.ferreteria.repository;

import com.kenpaku.ferreteria.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración de la capa DAO ({@link UserRepository}) contra H2 en memoria.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByUsername() encuentra un usuario existente")
    void findByUsernameExistente() {
        userRepository.save(new User("juan", "secreta", "juan@kenpaku.com", "USER"));

        Optional<User> encontrado = userRepository.findByUsername("juan");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("juan@kenpaku.com");
        assertThat(encontrado.get().getActivo()).isTrue();
    }

    @Test
    @DisplayName("findByUsername() devuelve vacío si no existe")
    void findByUsernameInexistente() {
        Optional<User> encontrado = userRepository.findByUsername("fantasma");

        assertThat(encontrado).isEmpty();
    }
}
