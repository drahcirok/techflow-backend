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

@Service
@RequiredArgsConstructor // Inyecta automáticamente los 'final' (Lombok)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // 1. Crear el usuario con la contraseña encriptada
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 🔐 ¡Importante!
                .role(request.getRole())
                .active(true)
                .build();

        // 2. Guardar en BD
        userRepository.save(user);

        // 3. Generar token
        var jwtToken = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar (Spring Security verifica usuario y contraseña por nosotros)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si pasó la línea anterior, es válido. Generamos token.
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}