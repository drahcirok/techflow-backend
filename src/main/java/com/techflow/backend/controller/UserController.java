package com.techflow.backend.controller;

import com.techflow.backend.dto.UpdateProfileRequest;
import com.techflow.backend.entity.User;
import com.techflow.backend.enums.Role;
import com.techflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("name", user.getName());
                    map.put("email", user.getEmail());
                    map.put("role", user.getRole());
                    map.put("active", user.isActive());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(users);
    }

    // 🔵 GET PERFIL DEL USUARIO AUTENTICADO
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile(@AuthenticationPrincipal User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("role", user.getRole());
        return ResponseEntity.ok(profile);
    }

    // 🔵 PUT ACTUALIZAR PERFIL DEL USUARIO AUTENTICADO
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request) {

        // Actualizar solo los campos permitidos
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User updated = userRepository.save(user);

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", updated.getId());
        profile.put("name", updated.getName());
        profile.put("email", updated.getEmail());
        profile.put("phone", updated.getPhone());
        profile.put("role", updated.getRole());
        return ResponseEntity.ok(profile);
    }

    // 🔵 GET CLIENTES (usuarios con rol CLIENTE)
    @GetMapping("/clients")
    public ResponseEntity<List<Map<String, Object>>> getClients() {
        List<Map<String, Object>> clients = userRepository.findByRoleOrderByNameAsc(Role.CLIENTE).stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("name", user.getName());
                    map.put("email", user.getEmail());
                    map.put("phone", user.getPhone());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(clients);
    }
}
