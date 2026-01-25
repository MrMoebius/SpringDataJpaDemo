# Explicación: ¿Qué es @Bean y por qué usarlo?

## ¿Qué es @Bean?

`@Bean` es una anotación de Spring que indica que un 
**método** produce un bean que será gestionado por el contenedor de Spring. 
El método debe estar dentro de una clase anotada con `@Configuration`.

## Diferencia Clave: @Bean vs @Component/@Service/@Repository

### Anotaciones de Clase (Stereotypes)

```java
@Service
public class ClienteService {
    // Spring crea una instancia de ESTA clase
}
```

**Características:**
- Se aplican a **clases que TÚ escribes**
- Spring crea una instancia de la clase anotada
- Usado para tus propias clases de negocio

### @Bean (Métodos de Configuración)

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Retornas una instancia de una clase que NO controlas
        return new PasswordEncoder() { ... };
    }
}
```

**Características:**
- Se aplica a **métodos** dentro de clases `@Configuration`
- El método **retorna** un objeto que Spring gestionará
- Usado para clases de terceros o configuración compleja

## ¿Por qué usar @Bean en lugar de @Component/@Service?

### Caso 1: Clases de Terceros (No puedes anotarlas)

**❌ NO puedes hacer esto:**
```java
// No puedes modificar la clase PasswordEncoder de Spring
@Component  // ← Esto no funciona, no es tu clase
public class PasswordEncoder { ... }
```

**✅ Usas @Bean:**
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Creas y configuras una instancia de la clase de terceros
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }
            // ...
        };
    }
}
```

### Caso 2: Configuración Compleja o Condicional

**Ejemplo de tu proyecto:**
```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());  // Configuración compleja
    return authProvider;
}
```

**¿Por qué no @Service?**
- `DaoAuthenticationProvider` es una clase de Spring Security
- Necesitas configurarlo con dependencias específicas
- No es una clase de negocio, es configuración

### Caso 3: Múltiples Instancias del Mismo Tipo

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new PasswordEncoder() { ... };
}

@Bean
public PasswordEncoder otroPasswordEncoder() {
    return new BCryptPasswordEncoder();  // Diferente implementación
}
```

Con `@Component` solo podrías tener una instancia por tipo.

## Ejemplos de tu Proyecto

### Ejemplo 1: PasswordEncoder Personalizado

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Clase anónima personalizada
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();  // No encripta
            }
            
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}
```

**¿Por qué @Bean?**
- `PasswordEncoder` es una interfaz de Spring Security
- Necesitas una implementación personalizada (no encripta)
- No puedes usar `@Component` porque es una clase anónima

### Ejemplo 2: DaoAuthenticationProvider

```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}
```

**¿Por qué @Bean?**
- `DaoAuthenticationProvider` es una clase de Spring Security
- Necesitas configurarlo con `userDetailsService` y `passwordEncoder`
- Es configuración, no lógica de negocio

### Ejemplo 3: SecurityFilterChain

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/css/**", "/login", "/error").permitAll()
        .requestMatchers("/empleados/**").hasRole("ADMIN")
        // ...
    );
    return http.build();
}
```

**¿Por qué @Bean?**
- `SecurityFilterChain` es una interfaz de Spring Security
- Necesitas configurarlo con `HttpSecurity`
- Es configuración de seguridad, no un servicio

## Comparación: @Bean vs @Service

| Aspecto           | @Bean                               | @Service/@Component            |
|-------------------|-------------------------------------|--------------------------------|
| **Ubicación**     | Método en clase `@Configuration`    | Clase completa                 |
| **Clase**         | Puede ser de terceros               | Debe ser tu clase              |
| **Control**       | Controlas la creación               | Spring crea la instancia       |
| **Configuración** | Puedes configurar antes de retornar | Spring usa constructor/setters |
| **Uso típico**    | Configuración, clases de terceros   | Lógica de negocio, tus clases  |

## Cuándo usar cada uno

### Usa @Component/@Service/@Repository cuando:

✅ Es **tu clase** de negocio
```java
@Service
public class ClienteService {
    // Tu lógica de negocio
}
```

✅ Spring puede crear la instancia automáticamente
```java
@Repository
public interface ClientesRepository extends JpaRepository<...> {
    // Spring genera la implementación
}
```

### Usa @Bean cuando:

✅ Es una **clase de terceros** (Spring Security, librerías externas)
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // Clase de Spring Security
}
```

✅ Necesitas **configuración compleja** antes de crear el bean
```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;  // Configurado antes de retornar
}
```

✅ Necesitas **múltiples instancias** del mismo tipo
```java
@Bean
public DataSource dataSource1() { ... }

@Bean
public DataSource dataSource2() { ... }
```

✅ Es una **clase anónima** o implementación inline
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new PasswordEncoder() { ... };  // Clase anónima
}
```

## Flujo de @Bean en tu Proyecto

```
1. Spring inicia la aplicación
   └─> Detecta @Configuration en SecurityConfig

2. Spring procesa métodos @Bean
   └─> Ejecuta passwordEncoder()
       └─> Crea instancia de PasswordEncoder personalizado
       └─> Lo registra en el contenedor

3. Spring inyecta el bean donde se necesite
   └─> authenticationProvider() recibe passwordEncoder()
   └─> SecurityFilterChain usa authenticationProvider()
```

## Resumen

**@Bean se usa cuando:**
- Necesitas crear beans de clases que **no controlas** (terceros)
- Requieres **configuración compleja** antes de crear el bean
- Quieres **control total** sobre cómo se crea el bean
- Necesitas **múltiples instancias** del mismo tipo

**@Service/@Component se usa cuando:**
- Es **tu clase** de negocio
- Spring puede crear la instancia automáticamente
- No necesitas configuración especial

## Ejemplo Completo de tu Proyecto

```java
@Configuration  // ← Clase de configuración
@EnableWebSecurity
public class SecurityConfig {
    
    // Inyección de dependencia (bean existente)
    private final UserDetailsService userDetailsService;
    
    // @Bean 1: PasswordEncoder personalizado
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Clase anónima personalizada
        return new PasswordEncoder() { ... };
    }
    
    // @Bean 2: DaoAuthenticationProvider configurado
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Usa userDetailsService (inyectado) y passwordEncoder() (otro @Bean)
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    // @Bean 3: SecurityFilterChain configurado
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Configuración compleja de seguridad
        http.authorizeHttpRequests(...)
            .formLogin(...)
            .logout(...)
            .authenticationProvider(authenticationProvider());
        return http.build();
    }
}
```

**¿Por qué no @Service?**
- `PasswordEncoder`, `DaoAuthenticationProvider`, `SecurityFilterChain` son clases/interfaces de Spring Security
- No son clases de negocio, son **configuración**
- Necesitas control sobre cómo se crean y configuran

---

## Pregunta de Examen: "¿Por qué @Bean y no @Service?"

**Respuesta:**
"Usamos 
`@Bean` porque estamos creando instancias de clases de Spring Security 
(`PasswordEncoder`, `DaoAuthenticationProvider`, `SecurityFilterChain`) que no controlamos. 
    Estas clases necesitan configuración específica antes de ser utilizadas. 
`@Service` se usa para nuestras propias clases de negocio como `ClienteService`, 
    donde Spring puede crear la instancia automáticamente mediante el constructor. 
`@Bean` nos da control total sobre la creación y configuración del bean."
