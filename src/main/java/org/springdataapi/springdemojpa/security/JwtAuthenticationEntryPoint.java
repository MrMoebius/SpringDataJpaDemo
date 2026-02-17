package org.springdataapi.springdemojpa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
// [SPRING SECURITY] Implementa AuthenticationEntryPoint de Spring Security
// Se ejecuta cuando una peticion no tiene token JWT valido y devuelve un error 401 en JSON
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // [SPRING SECURITY] Metodo que Spring Security llama cuando falla la autenticacion
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 401);
        body.put("error", "No autorizado");
        body.put("message", "Debe proporcionar un token JWT valido para acceder a este recurso");

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
