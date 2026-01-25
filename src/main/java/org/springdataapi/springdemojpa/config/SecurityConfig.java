package org.springdataapi.springdemojpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad de Spring Security.
 * Define autenticación, autorización y manejo de login/logout.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // PasswordEncoder personalizado que no encripta (las contraseñas en BD están en texto plano)
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Autorizaciones de rutas según roles
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin autenticación)
                .requestMatchers("/css/**", "/login", "/error").permitAll()
                // Solo ADMIN puede acceder a empleados
                .requestMatchers("/empleados/**").hasRole("ADMIN")
                // ADMIN y EMPLEADO pueden hacer CRUD completo de clientes y productos
                .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                .requestMatchers("/productos/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                .requestMatchers("/consultas/**").hasAnyRole("ADMIN", "EMPLEADO")
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
            )
            
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
