# Explicación: ¿Cómo funcionan las rutas en Spring MVC?

## Flujo de Trabajo Completo

```
┌──────────────────────────────────────────────────────────────┐
│ 1. Usuario hace clic o envía formulario                      │
│    <a th:href="@{/clientes}">Ver Clientes</a>                │
│    <form th:action="@{/clientes/guardar}" method="post">     │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. Thymeleaf genera la URL                                   │
│    @{/clientes} → "/clientes"                                │
│    @{/clientes/guardar} → "/clientes/guardar"                │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. Navegador envía petición HTTP                             │
│    GET /clientes                                             │
│    POST /clientes/guardar?nombre=Juan&email=juan@mail.com    │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. Spring MVC recibe la petición                             │
│    - Extrae: Ruta (/clientes)                                │
│    - Extrae: Método HTTP (GET)                               │
│    - Extrae: Parámetros (query string o body)                │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. Spring MVC busca el método que coincida                   │
│    Busca: @GetMapping("/clientes")                           │
│    En: ClienteController.listar()                            │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 6. Spring MVC mapea parámetros                               │
│    @RequestParam → Query string (?telefono=600)              │
│    @PathVariable → Variables en ruta (/{id}/editar)          │
│    @ModelAttribute → Objetos del formulario                  │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 7. Spring MVC ejecuta el método                              │
│    ClienteController.listar(model)                           │
│    └─> Llama al servicio                                     │
│    └─> Añade datos al modelo                                 │
│    └─> Retorna nombre de vista: "clientes/list"              │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 8. Thymeleaf renderiza la vista                              │
│    templates/clientes/list.html                              │
│    └─> Usa datos del modelo (${clientes})                    │
│    └─> Genera HTML final                                     │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 9. Navegador muestra la página                               │
└──────────────────────────────────────────────────────────────┘
```

## Conceptos Clave

### ❌ `@{/consultas/clientes}` NO es un constructor

Es una **expresión de URL de Thymeleaf** que genera una URL HTML:

```html
<!-- En Thymeleaf -->
<form th:action="@{/consultas/clientes}" method="get">

<!-- Se convierte en HTML -->
<form action="/consultas/clientes" method="get">
```

### ¿Cómo Spring MVC decide qué método ejecutar?

Spring MVC usa **3 factores** (en este orden):

1. **Ruta (Path)** - Lo más importante
   ```java
   @GetMapping("/consultas/clientes")  // ← Esta ruta debe coincidir
   ```

2. **Método HTTP** (GET, POST, PUT, DELETE)
   ```java
   @GetMapping("/clientes")   // Solo acepta GET
   @PostMapping("/clientes")  // Solo acepta POST
   ```

3. **Parámetros** - Solo se pasan como argumentos, NO determinan el método

## Tipos de Rutas

### 1. Rutas a nivel de método

```java
@Controller
public class ConsultasController {
    @GetMapping("/consultas/clientes")  // Ruta completa
    public String consultarClientes(...) { ... }
}
```

### 2. Rutas a nivel de clase con `@RequestMapping`

```java
@Controller
@RequestMapping("/clientes")  // ← Prefijo para todas las rutas
public class ClienteController {
    
    @GetMapping  // Equivale a GET /clientes
    public String listar(...) { ... }
    
    @GetMapping("/nuevo")  // Equivale a GET /clientes/nuevo
    public String nuevoCliente(...) { ... }
    
    @GetMapping("/{id}/editar")  // Equivale a GET /clientes/1/editar
    public String editarCliente(@PathVariable Integer id, ...) { ... }
}
```

**Ventajas:**
- Evita repetir el prefijo en cada método
- Organiza mejor el código
- Facilita el mantenimiento

## Tipos de Parámetros

### 1. `@RequestParam` - Query string

```java
@GetMapping("/consultas/clientes")
public String consultarClientes(
    @RequestParam(name = "telefono", required = false) String telefono) {
    // ...
}
```

**URL:** `/consultas/clientes?telefono=600` → `telefono = "600"`

### 2. `@PathVariable` - Variables en la ruta

```java
@GetMapping("/clientes/{id}/editar")
public String editar(@PathVariable Integer id) {
    // ...
}
```

**URL:** `/clientes/1/editar` → `id = 1`

### 3. `@ModelAttribute` - Objetos del formulario

```java
@PostMapping("/clientes/guardar")
public String guardar(@ModelAttribute("clienteDTO") ClientesDTO dto) {
    // Spring mapea automáticamente los campos del formulario
}
```

### 4. `@DateTimeFormat` - Formato de fechas

```java
@GetMapping("/consultas/clientes")
public String consultarClientes(
    @RequestParam(name = "fechaDesde", required = false) 
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde) {
    // Convierte "2024-01-01" → LocalDate
}
```

## Ejemplos Prácticos

### Ejemplo 1: Mismo método, diferentes parámetros

```java
@GetMapping("/consultas/clientes")
public String consultarClientes(
    @RequestParam(name = "telefono", required = false) String telefono,
    @RequestParam(name = "email", required = false) String email,
    @RequestParam(name = "tipoCliente", required = false) String tipoCliente,
    Model model) {
    // Este método se ejecuta SIEMPRE para /consultas/clientes
    // Los parámetros solo cambian los valores que recibe
}
```

**Todas estas URLs ejecutan el MISMO método:**
- `/consultas/clientes` → `consultarClientes(null, null, null, model)`
- `/consultas/clientes?telefono=600` → `consultarClientes("600", null, null, model)`
- `/consultas/clientes?telefono=600&email=test@gmail.com` → `consultarClientes("600", "test@gmail.com", null, model)`

### Ejemplo 2: Diferentes rutas = Diferentes métodos

```java
@GetMapping("/consultas/clientes")      // ← Ruta diferente
public String consultarClientes(...) { ... }

@GetMapping("/consultas/productos")      // ← Ruta diferente
public String consultarProductos(...) { ... }
```

### Ejemplo 3: Rutas con variables

```java
@Controller
@RequestMapping("/clientes")
public class ClienteController {
    
    @GetMapping("/{id}/editar")  // ← {id} es una variable
    public String editarCliente(@PathVariable Integer id, ...) { ... }
    
    @GetMapping("/{id}/eliminar")
    public String eliminarCliente(@PathVariable Integer id, ...) { ... }
}
```

**URLs:**
- `/clientes/1/editar` → `editarCliente(1, ...)`
- `/clientes/5/eliminar` → `eliminarCliente(5, ...)`

## ⚠️ Importante: Parámetros NO cambian el método

**Los parámetros NO determinan qué método se ejecuta**, solo se pasan como argumentos.

### ❌ Esto NO funciona:

```java
// No puedes hacer esto para cambiar de método
/consultas/clientes?parametro1=X  → Método A
/consultas/clientes?parametro2=Y  → Método B
```

### ✅ Esto SÍ funciona:

```java
// Usa rutas diferentes
@GetMapping("/consultas/clientes/por-empleado")  → Método A
@GetMapping("/consultas/clientes/por-fecha")     → Método B
```

## Soluciones para diferentes funcionalidades

### Opción 1: Rutas diferentes (Recomendado)

```java
@GetMapping("/consultas/clientes/por-empleado")
public String consultarClientesPorEmpleado(...) { ... }

@GetMapping("/consultas/clientes/por-fecha")
public String consultarClientesPorFecha(...) { ... }
```

### Opción 2: Filtros opcionales (Implementación actual)

```java
@GetMapping("/consultas/clientes")
public String consultarClientes(
    @RequestParam(name = "telefono", required = false) String telefono,
    @RequestParam(name = "email", required = false) String email,
    @RequestParam(name = "tipoCliente", required = false) String tipoCliente,
    Model model) {
    
    // Verificar si hay filtros activos
    boolean hayFiltros = telefono != null || email != null || tipoCliente != null;
    
    if (hayFiltros) {
        List<Clientes> clientes = clienteService.buscarClientesFiltrados(...);
        model.addAttribute("clientes", clientes);
    }
    
    return "consultas/clientes";
}
```

**Ventajas:**
- Un solo método maneja todas las combinaciones
- Flexible y fácil de mantener

## Resumen

| Concepto | Explicación |
|----------|-------------|
| `@{/ruta}` | Sintaxis Thymeleaf que genera una URL |
| **Ruta determina método** | `/clientes` → busca `@GetMapping("/clientes")` |
| **Parámetros NO cambian método** | Solo se pasan como argumentos |
| **Método HTTP importa** | GET, POST, PUT, DELETE son diferentes |
| `@RequestMapping` a nivel de clase | Prefijo común para todas las rutas |
| `@PathVariable` | Extrae valores de variables en la ruta (`/{id}/editar`) |
| `@RequestParam` | Extrae parámetros de la query string (`?telefono=600`) |
| `@DateTimeFormat` | Convierte strings a fechas automáticamente |

## Ejemplos Reales del Proyecto

### Consulta de Clientes con Filtros

```java
@GetMapping("/consultas/clientes")
public String consultarClientes(
    @RequestParam(name = "telefono", required = false) String telefono,
    @RequestParam(name = "email", required = false) String email,
    @RequestParam(name = "tipoCliente", required = false) String tipoCliente,
    @RequestParam(name = "idEmpleado", required = false) Integer idEmpleado,
    @RequestParam(name = "fechaDesde", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
    Model model) {
    // Este método se ejecuta SIEMPRE para /consultas/clientes
}
```

**Todas estas URLs ejecutan el mismo método:**
- `GET /consultas/clientes`
- `GET /consultas/clientes?telefono=600`
- `GET /consultas/clientes?telefono=600&email=test@gmail.com`
- `GET /consultas/clientes?tipoCliente=PARTICULAR&idEmpleado=1&fechaDesde=2024-01-01`

### CRUD de Clientes

```java
@Controller
@RequestMapping("/clientes")
public class ClienteController {
    
    @GetMapping  // GET /clientes
    public String listar(...) { ... }
    
    @GetMapping("/nuevo")  // GET /clientes/nuevo
    public String nuevoCliente(...) { ... }
    
    @GetMapping("/{id}/editar")  // GET /clientes/1/editar
    public String editarCliente(@PathVariable Integer id, ...) { ... }
    
    @PostMapping("/guardar")  // POST /clientes/guardar
    public String guardarCliente(...) { ... }
    
    @GetMapping("/{id}/eliminar")  // GET /clientes/1/eliminar
    public String eliminarCliente(@PathVariable Integer id, ...) { ... }
}
```

## Notas Importantes

1. **Orden de resolución**: Ruta → Método HTTP → Parámetros (solo en casos especiales)

2. **Ambigüedad**: Evita tener dos métodos con la misma ruta y método HTTP. Usa rutas diferentes.

3. **Thymeleaf**: `@{/ruta}` genera URLs relativas al contexto. Si tu app está en `/app`, `@{/clientes}` genera `/app/clientes`.

4. **Parámetros opcionales**: Usa `required = false` en `@RequestParam`. Si no se proporcionan, el valor será `null`.

5. **Formato de fechas**: Usa `@DateTimeFormat` para convertir automáticamente strings a `LocalDate`, `LocalDateTime`, etc.
