package com.techflow.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a TODAS las rutas
                        .allowedOrigins("http://localhost:5173", "http://localhost:3000") // 👈 AQUÍ ESTÁ LA CLAVE
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Métodos permitidos
                        .allowedHeaders("*") // Permitir todos los headers (como Authorization)
                        .allowCredentials(true); // Permitir cookies/credenciales si fuera necesario
            }

            @Override
            public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                // Servir archivos estáticos desde el directorio uploads
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:uploads/");
            }
        };
    }
}