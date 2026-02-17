package org.springdataapi.springdemojpa.config;

import org.springdataapi.springdemojpa.security.JwtAuthenticationEntryPoint;
import org.springdataapi.springdemojpa.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity   // [SPRING SECURITY] Habilita la configuracion de seguridad web
@EnableMethodSecurity // [SPRING SECURITY] Habilita anotaciones @PreAuthorize en controllers
public class SecurityConfig {

    // [SPRING SECURITY] Servicio que carga usuarios desde BD para autenticacion
    private final UserDetailsService userDetailsService;
    // [SPRING SECURITY] Maneja errores 401 cuando no hay token JWT valido
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    // [SPRING SECURITY] Filtro personalizado que intercepta peticiones y valida el JWT
    private final JwtAuthenticationFilter authenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter authenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    // [SPRING SECURITY] Bean que define como se codifican las contraseñas
    @Bean
    @SuppressWarnings("deprecation")
    public static PasswordEncoder passwordEncoder() {
        // TODO: Cambiar a BCryptPasswordEncoder cuando las contraseñas en BD esten encriptadas
        return NoOpPasswordEncoder.getInstance();
    }

    // [SPRING SECURITY] Bean que gestiona la autenticacion (valida credenciales)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // ==================== FILTER CHAIN 1: API REST (JWT Stateless) ====================
    // [SPRING SECURITY] Cadena de filtros para la API REST, usa JWT sin sesion (stateless)
    @Bean
    @Order(1) // [SPRING SECURITY] Prioridad 1: se evalua antes que la cadena web
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**") // [SPRING SECURITY] Solo aplica a rutas /api/**
                .csrf(csrf -> csrf.disable()) // [SPRING SECURITY] Deshabilita CSRF (no necesario en API stateless)
                .cors(Customizer.withDefaults()) // [SPRING SECURITY] Habilita CORS con la config definida abajo
                .authorizeHttpRequests(authorize -> authorize
                        // [SPRING SECURITY] Reglas de autorizacion por URL y rol
                        .requestMatchers("/api/auth/**").permitAll()  // Publico: login sin token
                        .requestMatchers("/api/empleados/**").hasRole("ADMIN") // Solo ADMIN
                        .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                        .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                        .requestMatchers("/api/consultas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .anyRequest().authenticated() // El resto requiere autenticacion
                )
                .exceptionHandling(exception -> exception
                        // [SPRING SECURITY] Cuando falla la autenticacion, responde con 401 JSON
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        // [SPRING SECURITY] No crea sesion HTTP, cada peticion se valida con JWT
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // [SPRING SECURITY] Registra nuestro filtro JWT antes del filtro de usuario/password por defecto
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // ==================== FILTER CHAIN 2: Web Thymeleaf (Form Login + Session) ====================
    // [SPRING SECURITY] Cadena de filtros para la web, usa sesion con form login
    @Bean
    @Order(2) // [SPRING SECURITY] Prioridad 2: se evalua despues de la cadena API
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // [SPRING SECURITY] Reglas de autorizacion por URL y rol para la web
                        .requestMatchers("/css/**", "/login", "/error").permitAll() // Recursos publicos
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/empleados/**").hasRole("ADMIN") // Solo ADMIN
                        .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                        .requestMatchers("/productos/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                        .requestMatchers("/consultas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .anyRequest().authenticated()
                )
                // [SPRING SECURITY] Configura el formulario de login
                .formLogin(form -> form
                        .loginPage("/login")           // Pagina de login personalizada
                        .defaultSuccessUrl("/", true)  // Redirige al index tras login exitoso
                        .permitAll()
                )
                // [SPRING SECURITY] Configura el logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    // [SPRING SECURITY] Configuracion CORS para permitir peticiones desde frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
