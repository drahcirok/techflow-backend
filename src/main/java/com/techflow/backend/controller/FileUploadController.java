package com.techflow.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private Path getUploadDir() {
        // Ruta absoluta basada en el directorio del proyecto
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "products");
        File dir = uploadDir.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return uploadDir;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validar que no esté vacío
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
            }

            // Validar que sea una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten imágenes"));
            }

            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "La imagen no puede superar 5MB"));
            }

            // Generar nombre único
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            } else {
                extension = ".jpg";
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Guardar archivo con ruta absoluta
            Path uploadDir = getUploadDir();
            Path filePath = uploadDir.resolve(filename);
            Files.write(filePath, file.getBytes());

            System.out.println("✅ Imagen guardada en: " + filePath.toAbsolutePath());

            // Retornar URL relativa
            String imageUrl = "/uploads/products/" + filename;

            return ResponseEntity.ok(Map.of(
                    "url", imageUrl,
                    "filename", filename
            ));

        } catch (IOException e) {
            System.err.println("❌ Error al guardar imagen: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al guardar la imagen: " + e.getMessage()));
        }
    }
}
