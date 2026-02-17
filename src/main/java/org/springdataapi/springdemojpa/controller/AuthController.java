package org.springdataapi.springdemojpa.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.LoginDto;
import org.springdataapi.springdemojpa.repository.ClientesRepository;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springdataapi.springdemojpa.security.JwtTokenProvider;
import org.springdataapi.springdemojpa.security.LoginRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // [SPRING SECURITY] AuthenticationManager valida las credenciales (email/password)
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginRateLimiter loginRateLimiter;
    private final ClientesRepository clientesRepository;
    private final EmpleadosRepository empleadosRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          LoginRateLimiter loginRateLimiter,
                          ClientesRepository clientesRepository,
                          EmpleadosRepository empleadosRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginRateLimiter = loginRateLimiter;
        this.clientesRepository = clientesRepository;
        this.empleadosRepository = empleadosRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto loginDto,
                                                      HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String key = loginDto.getEmail() + ":" + ip;

        if (loginRateLimiter.isBlocked(key) || loginRateLimiter.isBlocked(ip)) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Demasiados intentos fallidos. Cuenta bloqueada temporalmente.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        }

        try {
            // [SPRING SECURITY] Autentica al usuario con email y password usando AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

            // [SPRING SECURITY] Guarda la autenticacion en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            loginRateLimiter.registerSuccessfulLogin(key);
            loginRateLimiter.registerSuccessfulLogin(ip);

            String token = jwtTokenProvider.generateToken(authentication);

            // [SPRING SECURITY] Obtiene el rol del usuario desde las authorities de Spring Security
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(item -> item.getAuthority())
                    .orElse("ROLE_CLIENTE");

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", token);
            response.put("tokenType", "Bearer");
            response.put("role", role);
            response.put("email", authentication.getName());

            // Incluir nombre e id del usuario
            empleadosRepository.findByEmail(authentication.getName())
                    .ifPresentOrElse(
                            empleado -> {
                                response.put("nombre", empleado.getNombre());
                                response.put("userId", empleado.getId());
                            },
                            () -> clientesRepository.findByEmail(authentication.getName())
                                    .ifPresent(cliente -> {
                                        response.put("nombre", cliente.getNombre());
                                        response.put("userId", cliente.getId());
                                    })
                    );

            return ResponseEntity.ok(response);
        // [SPRING SECURITY] BadCredentialsException: excepcion de Spring Security cuando el password es incorrecto
        } catch (BadCredentialsException e) {
            loginRateLimiter.registerFailedAttempt(key);
            loginRateLimiter.registerFailedAttempt(ip);
            throw e;
        }
    }
}
