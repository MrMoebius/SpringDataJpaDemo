# SpringDemoJpa

Aplicación web Spring Boot para gestión de clientes, empleados y productos con control de acceso basado en roles.

## 🚀 Tecnologías

- **Spring Boot 4.0.1**
- **Spring Data JPA** - Acceso a datos
- **Spring Security** - Autenticación y autorización
- **Thymeleaf** - Motor de plantillas
- **MySQL** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## 📋 Características

### Gestión de Entidades
- **Clientes**: CRUD completo con validaciones
- **Empleados**: Gestión de empleados con roles
- **Productos**: Catálogo de productos con categorías

### Control de Acceso por Roles
- **ADMIN**: Acceso completo a todas las funcionalidades
- **EMPLEADO**: Gestión de clientes y productos (CRUD completo), consultas
- **CLIENTE**: Solo lectura de su información y productos disponibles

### Consultas y Filtros
- Búsqueda avanzada de clientes (teléfono, email, tipo, empleado, fecha)
- Filtros para empleados (teléfono, email, estado, rol, fecha ingreso)
- Consulta de productos con filtros (nombre, categoría, precio, activo)

## 🏗️ Estructura del Proyecto

```
src/main/java/org/springdataapi/springdemojpa/
├── config/
│   └── SecurityConfig.java          # Configuración de seguridad
├── controller/
│   ├── ClienteController.java      # Controlador de clientes
│   ├── EmpleadosController.java    # Controlador de empleados
│   ├── ProductosController.java    # Controlador de productos
│   └── ConsultasController.java    # Controlador de consultas
├── models/
│   ├── Clientes.java               # Entidad Clientes
│   ├── Empleados.java              # Entidad Empleados
│   ├── Productos.java              # Entidad Productos
│   └── RolesEmpleado.java          # Entidad Roles
├── repository/
│   ├── ClientesRepository.java     # Repositorio JPA
│   ├── EmpleadosRepository.java
│   └── ProductosRepository.java
├── service/
│   ├── ClienteService.java         # Lógica de negocio
│   ├── EmpleadosService.java
│   └── ProductosService.java
└── security/
    ├── CustomUserDetails.java      # Implementación UserDetails
    └── CustomUserDetailsService.java
```

## ⚙️ Configuración

### Base de Datos

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm_xtart
spring.datasource.username=root
spring.datasource.password=tu_password
```

### Crear Base de Datos

```sql
CREATE DATABASE crm_xtart;
```

Spring Boot creará las tablas automáticamente con `spring.jpa.hibernate.ddl-auto=update`.

## 🏃 Ejecución

### Requisitos
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Pasos

1. Clonar el repositorio
2. Configurar la base de datos en `application.properties`
3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
4. Acceder a: `http://localhost:8080`

### Credenciales por Defecto

- **Usuario**: `admin@admin.es`
- **Contraseña**: `1234`

## 📚 Documentación

- [Flujo de Rutas en Spring MVC](docs/explicacion-rutas-spring.md)
- [Flujo de Spring Data JPA](docs/flujo-spring-data-jpa.md)

## 🔐 Seguridad

- Autenticación mediante email
- Roles: ADMIN, EMPLEADO, CLIENTE
- Acceso restringido por URL según rol
- Password encoder personalizado (NoOp para desarrollo)

## 📝 Notas

- Las contraseñas se almacenan en texto plano (solo para desarrollo)
- El proyecto usa `@Data` de Lombok para generar getters/setters automáticamente
- Las consultas usan JPQL para flexibilidad en filtros dinámicos
