package org.springdataapi.springdemojpa.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * [SPRING SECURITY] Implementacion personalizada de UserDetails.
 * UserDetails es la interfaz de Spring Security que representa al usuario autenticado.
 * Aqui definimos los datos del usuario (email, password, rol) y sus permisos (authorities).
 */
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role;
    private Integer userId;
    private String userType; // "ADMIN", "EMPLEADO", "CLIENTE"

    public CustomUserDetails(String username, String password, String role, Integer userId, String userType) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.userId = userId;
        this.userType = userType;
    }

    // [SPRING SECURITY] Devuelve los permisos/roles del usuario (ej: ROLE_ADMIN)
    // Spring Security usa esto para evaluar @PreAuthorize y hasRole()
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // [SPRING SECURITY] Metodos obligatorios de UserDetails
    // Devuelven true para indicar que la cuenta esta activa y no bloqueada
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getRole() {
        return role;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }
}
