package com.techflow.backend.entity;

import com.techflow.backend.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails { // 👈 1. IMPORTANTE: Implementamos UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean active = true;

    // 👇 2. MÉTODOS MÁGICOS DE SEGURIDAD (Esto arregla el error 403) 👇

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 🔥 AQUÍ ESTÁ LA SOLUCIÓN:
        // Spring Security exige que los roles empiecen con "ROLE_"
        // Convertimos tu "ADMIN" (BD) a "ROLE_ADMIN" (Seguridad) automáticamente.
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // Le decimos a Spring que el "usuario" es el email
    }

    @Override
    public String getPassword() {
        return password; // La contraseña ya la tienes arriba
    }

    // Estos 4 métodos son para cuentas que expiran o se bloquean.
    // Devolvemos 'true' para decir "la cuenta está perfecta".
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active; // Si active es false, Spring bloqueará el login automáticamente
    }
}