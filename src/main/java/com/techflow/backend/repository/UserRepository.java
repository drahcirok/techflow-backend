package com.techflow.backend.repository;

import com.techflow.backend.entity.User;
import com.techflow.backend.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método mágico: Spring crea el SQL "SELECT * FROM users WHERE email = ?"
    Optional<User> findByEmail(String email);

    // Para verificar si un email ya existe antes de registrar
    boolean existsByEmail(String email);

    // Para obtener usuarios por rol (clientes, técnicos, etc.)
    List<User> findByRoleOrderByNameAsc(Role role);
}