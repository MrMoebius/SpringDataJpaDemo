package org.springdataapi.springdemojpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad de Spring Security.
 * Define autenticación, autorización y manejo de login/logout.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            // Autorizaciones de rutas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin autenticación)
                .requestMatchers("/css/**", "/login", "/error").permitAll()
                // Resto de rutas requieren autenticación
                .anyRequest().authenticated()
            )
            
            // Configuración de login
            .formLogin(form -> form
                .loginPage("/login")  // Página personalizada de login
                .defaultSuccessUrl("/", true)  // Redirige a "/" después del login
                .permitAll()
            )
            
            // Configuración de logout
            .logout(logout -> logout
                .logoutUrl("/logout")  // URL para cerrar sesión (POST)
                .logoutSuccessUrl("/login?logout")  // Redirige al login después del logout
                .permitAll()
            );

        return http.build();
    }
}
