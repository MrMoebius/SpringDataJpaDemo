# Diagrama de Flujo: Consulta de Clientes con Filtros

## Descripción
Este documento explica el flujo completo desde la vista HTML hasta la ejecución de la query en la base de datos para la funcionalidad de consulta de clientes con sistema de filtros avanzado.

## Flujo Completo

```
┌──────────────────────────────────────────────────────────────┐
│ 1. clientes.html (Vista)                                     │
│    <form th:action="@{/consultas/clientes}" method="get">    │
│    └─> Campos de filtro:                                     │
│        - telefono (texto, búsqueda parcial)                  │
│        - email (texto, búsqueda parcial)                     │
│        - tipoCliente (select: PARTICULAR/EMPRESA)            │
│        - idEmpleado (select: empleados)                      │
│        - fechaDesde (date)                                   │
│    └─> Envía: ?telefono=X&email=Y&tipoCliente=Z&...          │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. ConsultasController.consultarClientes()                   │
│    @GetMapping("/consultas/clientes")                        │
│    └─> Recibe parámetros opcionales del request              │
│    └─> Normaliza parámetros (trim, null si vacío)            │
│    └─> Verifica si hay filtros activos                       │
│    └─> Llama: clienteService.buscarClientesFiltrados()       │
│    └─> Pasa lista de empleados al modelo                     │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. ClienteService.buscarClientesFiltrados()                  │
│    └─> Delega a: clientesRepository.buscarClientesFiltrados()│
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. ClientesRepository.buscarClientesFiltrados()              │
│    @Query (JPQL)                                             │
│    └─> Ejecuta query SQL dinámica con filtros opcionales     │
│    └─> Retorna: List<Clientes> ordenada por ID               │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. Vuelve al controlador                                     │
│    └─> model.addAttribute("clientes", clientes)              │
│    └─> model.addAttribute("empleados", empleados)            │
│    └─> Mantiene valores de filtros en el modelo              │
│    └─> return "consultas/clientes"                           │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 6. clientes.html renderiza los resultados                    │
│    <tr th:each="cliente : ${clientes}">                      │
│    └─> Muestra tabla con: ID, Nombre, Email, Teléfono,       │
│        Tipo Cliente, Fecha Alta, Empleado Responsable        │
└──────────────────────────────────────────────────────────────┘
```

## Detalle por Capa

### 1. Vista (clientes.html)

**Ubicación:** `src/main/resources/templates/consultas/clientes.html`

**Responsabilidad:**
- Mostrar formulario de búsqueda con múltiples filtros opcionales
- Enviar parámetros mediante GET
- Renderizar resultados en tabla
- Mantener valores de filtros en los campos del formulario

**Campos del formulario:**
```html
<form th:action="@{/consultas/clientes}" method="get">
    <!-- Teléfono: búsqueda parcial -->
    <input type="text" name="telefono" th:value="${telefono}">
    
    <!-- Email: búsqueda parcial -->
    <input type="email" name="email" th:value="${email}">
    
    <!-- Tipo de Cliente: select con opciones -->
    <select name="tipoCliente">
        <option value="">-- Todos --</option>
        <option value="PARTICULAR">PARTICULAR</option>
        <option value="EMPRESA">EMPRESA</option>
    </select>
    
    <!-- Empleado Responsable: select dinámico -->
    <select name="idEmpleado">
        <option value="">-- Todos --</option>
        <option th:each="empleado : ${empleados}" 
                th:value="${empleado.id}" 
                th:text="${empleado.nombre}">
        </option>
    </select>
    
    <!-- Fecha desde: date picker -->
    <input type="date" name="fechaDesde" th:value="${fechaDesde}">
    
    <button type="submit">Buscar</button>
    <a th:href="@{/consultas/clientes}">Limpiar</a>
</form>
```

### 2. Controlador (ConsultasController)

**Ubicación:** `src/main/java/org/springdataapi/springdemojpa/controller/ConsultasController.java`

**Método:** `consultarClientes()`

**Responsabilidad:**
- Recibir parámetros opcionales de la petición HTTP
- Normalizar parámetros (trim, convertir a null si están vacíos)
- Verificar si hay al menos un filtro activo
- Cargar lista de empleados para el dropdown
- Invocar el servicio con los filtros
- Pasar datos al modelo para la vista
- Mantener valores de filtros en el modelo para rellenar el formulario

**Código clave:**
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
    
    // Cargar empleados para el dropdown
    model.addAttribute("empleados", empleadosService.findAll());
    
    // Normalizar parámetros (trim y null si vacío)
    String telefonoNormalizado = (telefono != null && !telefono.trim().isEmpty()) 
        ? telefono.trim() : null;
    String emailNormalizado = (email != null && !email.trim().isEmpty()) 
        ? email.trim() : null;
    String tipoClienteNormalizado = (tipoCliente != null && !tipoCliente.trim().isEmpty()) 
        ? tipoCliente.trim() : null;
    
    // Verificar si hay algún filtro activo
    boolean hayFiltros = telefonoNormalizado != null ||
                        emailNormalizado != null ||
                        tipoClienteNormalizado != null ||
                        idEmpleado != null ||
                        fechaDesde != null;
    
    if (hayFiltros) {
        List<Clientes> clientes = clienteService.buscarClientesFiltrados(
            telefonoNormalizado, emailNormalizado, tipoClienteNormalizado, 
            idEmpleado, fechaDesde);
        model.addAttribute("clientes", clientes);
    } else {
        model.addAttribute("clientes", null);
    }
    
    // Mantener valores en el formulario
    model.addAttribute("telefono", telefono);
    model.addAttribute("email", email);
    model.addAttribute("tipoCliente", tipoCliente);
    model.addAttribute("idEmpleado", idEmpleado);
    model.addAttribute("fechaDesde", fechaDesde);
    
    return "consultas/clientes";
}
```

### 3. Servicio (ClienteService)

**Ubicación:** `src/main/java/org/springdataapi/springdemojpa/service/ClienteService.java`

**Método:** `buscarClientesFiltrados()`

**Responsabilidad:**
- Capa de lógica de negocio
- Delegar al repositorio
- Puede incluir validaciones adicionales

**Código clave:**
```java
public List<Clientes> buscarClientesFiltrados(
        String telefono, String email, String tipoCliente, 
        Integer idEmpleado, LocalDate fechaDesde) {
    return clientesRepository.buscarClientesFiltrados(
        telefono, email, tipoCliente, idEmpleado, fechaDesde);
}
```

### 4. Repositorio (ClientesRepository)

**Ubicación:** `src/main/java/org/springdataapi/springdemojpa/repository/ClientesRepository.java`

**Método:** `buscarClientesFiltrados()`

**Responsabilidad:**
- Acceso a datos
- Ejecutar query JPQL con filtros dinámicos
- Mapear resultados a entidades
- Ordenar resultados por ID

**Código clave:**
```java
@Query("""
    SELECT c
    FROM Clientes c
    WHERE (:telefono IS NULL OR :telefono = '' OR c.telefono LIKE CONCAT('%', :telefono, '%'))
      AND (:email IS NULL OR :email = '' OR c.email LIKE CONCAT('%', :email, '%'))
      AND (:tipoCliente IS NULL OR :tipoCliente = '' OR c.tipoCliente = :tipoCliente)
      AND (:idEmpleado IS NULL OR c.idEmpleadoResponsable.id = :idEmpleado)
      AND (:fechaDesde IS NULL OR c.fechaAlta >= :fechaDesde)
    ORDER BY c.id
""")
public List<Clientes> buscarClientesFiltrados(
        @Param("telefono") String telefono,
        @Param("email") String email,
        @Param("tipoCliente") String tipoCliente,
        @Param("idEmpleado") Integer idEmpleado,
        @Param("fechaDesde") LocalDate fechaDesde);
```

## Query JPQL Ejecutada

La query JPQL se traduce a SQL de la siguiente manera:

**JPQL:**
```sql
SELECT c
FROM Clientes c
WHERE (:telefono IS NULL OR :telefono = '' OR c.telefono LIKE CONCAT('%', :telefono, '%'))
  AND (:email IS NULL OR :email = '' OR c.email LIKE CONCAT('%', :email, '%'))
  AND (:tipoCliente IS NULL OR :tipoCliente = '' OR c.tipoCliente = :tipoCliente)
  AND (:idEmpleado IS NULL OR c.idEmpleadoResponsable.id = :idEmpleado)
  AND (:fechaDesde IS NULL OR c.fechaAlta >= :fechaDesde)
ORDER BY c.id
```

**SQL equivalente (ejemplo con todos los filtros):**
```sql
SELECT * 
FROM clientes c
WHERE c.telefono LIKE '%600%'
  AND c.email LIKE '%@gmail.com%'
  AND c.tipo_cliente = 'PARTICULAR'
  AND c.id_empleado_responsable = 1
  AND c.fecha_alta >= '2024-01-01'
ORDER BY c.id_cliente
```

**SQL equivalente (ejemplo sin filtros):**
```sql
SELECT * 
FROM clientes c
ORDER BY c.id_cliente
```

## Parámetros

Todos los parámetros son **opcionales**. La query se adapta dinámicamente según los filtros proporcionados:

- **telefono** (String, opcional): Búsqueda parcial con `LIKE`. Si es `null` o vacío, se ignora.
- **email** (String, opcional): Búsqueda parcial con `LIKE`. Si es `null` o vacío, se ignora.
- **tipoCliente** (String, opcional): Valores válidos: `"PARTICULAR"`, `"EMPRESA"`. Si es `null` o vacío, se ignora.
- **idEmpleado** (Integer, opcional): ID del empleado responsable. Si es `null`, se ignora.
- **fechaDesde** (LocalDate, opcional): Fecha mínima de alta del cliente. Si es `null`, se ignora.

## Comportamiento de los Filtros

1. **Filtros de texto (teléfono, email)**: 
   - Usan `LIKE` con `CONCAT('%', :param, '%')` para búsqueda parcial
   - Si el parámetro es `null` o cadena vacía, la condición se omite

2. **Filtro de tipo de cliente**:
   - Comparación exacta (`=`)
   - Solo acepta `"PARTICULAR"` o `"EMPRESA"`

3. **Filtro de empleado**:
   - Comparación exacta por ID
   - Accede a la relación `idEmpleadoResponsable.id`

4. **Filtro de fecha**:
   - Comparación `>=` (mayor o igual)
   - Si se proporciona, muestra clientes con fecha de alta igual o posterior

5. **Ordenamiento**:
   - Los resultados siempre se ordenan por `c.id` (ascendente)

## Resultado

Retorna una lista de objetos `Clientes` que cumplen con **todos** los criterios de filtro activos:
- Si no se proporciona ningún filtro, no se ejecuta la búsqueda (retorna `null` en el modelo)
- Si se proporcionan filtros, retorna los clientes que coinciden con **todos** los criterios (AND lógico)
- Los resultados están ordenados por ID ascendente

## Notas Importantes

1. **Patrón MVC**: La vista no conoce directamente el servicio ni el repositorio. El controlador actúa como intermediario.

2. **Separación de responsabilidades**:
   - **Vista**: Presentación y captura de datos
   - **Controlador**: Coordinación, normalización y validación de parámetros
   - **Servicio**: Lógica de negocio
   - **Repositorio**: Acceso a datos con queries JPQL

3. **Thymeleaf**: La vista usa Thymeleaf para:
   - Renderizar datos del modelo (`${clientes}`, `${empleados}`, etc.)
   - Mantener valores en formularios (`th:value`, `th:selected`)
   - Iterar sobre colecciones (`th:each`)

4. **Spring Data JPA**: El repositorio usa Spring Data JPA con query personalizada mediante `@Query` y parámetros nombrados con `@Param`.

5. **Filtros dinámicos**: La query JPQL usa condiciones `IS NULL OR = ''` para hacer los filtros opcionales, permitiendo que la misma query funcione con cualquier combinación de filtros.

6. **Normalización**: El controlador normaliza los parámetros antes de pasarlos al servicio, convirtiendo cadenas vacías en `null` para simplificar la lógica de la query.
