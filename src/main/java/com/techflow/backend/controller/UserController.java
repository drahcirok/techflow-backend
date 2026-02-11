package com.techflow.backend.controller;

import com.techflow.backend.dto.UpdateProfileRequest;
import com.techflow.backend.entity.User;
import com.techflow.backend.enums.Role;
import com.techflow.backend.repository.ServiceOrderRepository;
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
    private final ServiceOrderRepository serviceOrderRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("name", user.getName());
                    map.put("email", user.getEmail());
                    map.put("phone", user.getPhone());
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

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.containsKey("name") && request.get("name") != null && !request.get("name").isBlank()) {
            user.setName(request.get("name"));
        }
        if (request.containsKey("email") && request.get("email") != null && !request.get("email").isBlank()) {
            user.setEmail(request.get("email"));
        }
        if (request.containsKey("phone")) {
            user.setPhone(request.get("phone"));
        }
        if (request.containsKey("role") && request.get("role") != null) {
            user.setRole(Role.valueOf(request.get("role")));
        }
        if (request.containsKey("active") && request.get("active") != null) {
            user.setActive(Boolean.parseBoolean(request.get("active")));
        }

        User updated = userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", updated.getId());
        result.put("name", updated.getName());
        result.put("email", updated.getEmail());
        result.put("phone", updated.getPhone());
        result.put("role", updated.getRole());
        result.put("active", updated.isActive());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/technician-ratings")
    public ResponseEntity<Map<Long, Map<String, Object>>> getTechnicianRatings() {
        List<Object[]> results = serviceOrderRepository.findAverageRatingGroupedByTechnician();
        Map<Long, Map<String, Object>> ratings = new HashMap<>();

        for (Object[] row : results) {
            Long techId = (Long) row[0];
            Double average = (Double) row[1];
            Long count = (Long) row[2];

            Map<String, Object> data = new HashMap<>();
            data.put("average", Math.round(average * 10.0) / 10.0);
            data.put("count", count);
            ratings.put(techId, data);
        }

        return ResponseEntity.ok(ratings);
    }
}
