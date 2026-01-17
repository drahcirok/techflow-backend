package com.techflow.backend.dto;

import com.techflow.backend.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role; // ADMIN, TECNICO, CLIENTE
}