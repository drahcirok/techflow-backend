package com.techflow.backend.service;

import com.techflow.backend.dto.AuthResponse;
import com.techflow.backend.dto.LoginRequest;
import com.techflow.backend.dto.RegisterRequest;
import com.techflow.backend.entity.User;
import com.techflow.backend.repository.UserRepository;
import com.techflow.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 0. Validar campos requeridos
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("El nombre es requerido");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("El correo electrónico es requerido");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es requerida");
        }

        // 1. Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // 2. Crear el usuario con la contraseña encriptada
        // Si no viene rol, usar CLIENTE por defecto
        var role = request.getRole() != null ? request.getRole() : com.techflow.backend.enums.Role.CLIENTE;

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(true)
                .build();

        // 2. Guardar en BD
        userRepository.save(user);

        // 3. Generar token
        // 🔴 CORREGIDO: Pasamos el objeto 'user' completo, no solo el email
        var jwtToken = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar (Spring Security verifica usuario y contraseña por nosotros)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si pasó la línea anterior, es válido. Recuperamos el usuario completo.
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        // 🔴 CORREGIDO: Pasamos el objeto 'user' completo
        var jwtToken = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}