package com.techflow.backend.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        String message = "Error de base de datos";

        // Obtener el mensaje de la causa raíz
        String rootMessage = ex.getMostSpecificCause().getMessage();
        System.err.println("⚠️ DataIntegrityViolationException: " + rootMessage);

        if (rootMessage != null) {
            if (rootMessage.contains("Duplicate") && rootMessage.contains("email")) {
                message = "El correo electrónico ya está registrado";
            } else if (rootMessage.contains("Duplicate") && rootMessage.contains("tracking_code")) {
                message = "Error al generar código de seguimiento, intenta de nuevo";
            } else if (rootMessage.contains("cannot be null")) {
                message = "Faltan campos obligatorios";
            } else {
                message = "Error de base de datos: " + rootMessage;
            }
        }

        response.put("message", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Error en los datos enviados. Verifica el formato.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Error interno del servidor: " + ex.getMessage());
        ex.printStackTrace(); // Para ver el error en los logs del backend
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
