package com.ticketlite.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class corsConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // desactiva CSRF
                .authorizeHttpRequests(
                        authorization -> authorization
                .anyRequest().permitAll()
                );// permite todas las peticiones sin autenticación
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")//Permite todos los orígenes
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //Metodos permitidos
                        .allowedHeaders("*") //Permite todos los headers
                        .allowCredentials(true); //Cambia a true si usas cookies/sesiones
            }
        };

    }

}
