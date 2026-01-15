package com.techflow.backend.entity;

import com.techflow.backend.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "users") // Nombre de la tabla en MySQL
@Data // Lombok genera Getters, Setters, ToString, etc. automágicamente
@NoArgsConstructor // Constructor vacío (necesario para JPA)
@AllArgsConstructor // Constructor con todo
@Builder // Patrón Builder para crear objetos fácilmente
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (1, 2, 3...)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Aquí guardaremos la contraseña encriptada

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // Guarda "ADMIN" como texto en la BD
    @Column(nullable = false)
    private Role role;

    private boolean active = true; // Para borrado lógico (no borrar usuarios, solo desactivarlos)
}
