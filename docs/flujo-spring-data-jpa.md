# Flujo de Funcionamiento de Spring Data JPA

## Flujo de Trabajo Completo

```
┌──────────────────────────────────────────────────────────────┐
│ 1. Controlador recibe petición HTTP                          │
│    ClienteController.listar()                                │
│    └─> Llama: clienteService.findAll()                       │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. Servicio ejecuta lógica de negocio                        │
│    ClienteService.findAll()                                  │
│    └─> Llama: clientesRepository.findAll()                   │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. Spring Data JPA intercepta la llamada                     │
│    ClientesRepository.findAll()                              │
│    └─> Spring detecta que es método de JpaRepository         │
│    └─> Genera implementación dinámica                        │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. Spring analiza el método                                  │
│    Tipo A: Método heredado (findAll, findById, save...)      │
│    Tipo B: Método derivado (findByEmail, existsByTelefono)   │
│    Tipo C: Método con @Query (JPQL personalizado)            │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. Spring genera/ejecuta la query                            │
│    Tipo A: Usa query predefinida de JpaRepository            │
│    Tipo B: Genera query desde el nombre del método           │
│    Tipo C: Usa la query JPQL especificada en @Query          │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 6. Hibernate convierte JPQL a SQL                            │
│    JPQL: SELECT c FROM Clientes c                            │
│    SQL:  SELECT * FROM clientes                              │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 7. Se ejecuta la query SQL en la base de datos               │
│    MySQL ejecuta: SELECT * FROM clientes                     │
│    └─> Retorna: ResultSet con filas                          │ 
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 8. Hibernate mapea resultados a entidades                    │
│    ResultSet → Objetos Clientes                              │
│    └─> Usa anotaciones @Entity, @Column, @ManyToOne          │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 9. Retorna resultado al servicio                             │
│    List<Clientes> o Optional<Clientes>                       │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│ 10. Servicio retorna al controlador                          │
│     Controlador añade datos al modelo                        │
│     └─> return "clientes/list"                               │
└──────────────────────────────────────────────────────────────┘
```

## Conceptos Clave

### ¿Qué es Spring Data JPA?

Spring Data JPA es una **abstracción sobre JPA/Hibernate** que simplifica el acceso a datos:

- **No escribes implementaciones**: Solo defines interfaces
- **Genera queries automáticamente**: Desde nombres de métodos
- **Reduce código boilerplate**: CRUD básico sin código

### Componentes Principales

1. **Entidad (Entity)**: Clase Java mapeada a tabla de BD (`@Entity`)
2. **Repositorio (Repository)**: Interface que extiende `JpaRepository`
3. **Servicio (Service)**: Lógica de negocio que usa el repositorio
4. **Controlador (Controller)**: Recibe peticiones HTTP y usa el servicio

## Tipos de Métodos en Spring Data JPA

### 1. Métodos Heredados de JpaRepository

Spring Data JPA proporciona métodos predefinidos sin necesidad de implementarlos:

```java
@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Integer> {
    // Métodos disponibles automáticamente:
    // - findAll()
    // - findById(Integer id)
    // - save(Clientes cliente)
    // - deleteById(Integer id)
    // - existsById(Integer id)
    // - count()
}
```

**Ejemplo:**
```java
@Service
public class ClienteService {
    public List<Clientes> findAll() {
        return clientesRepository.findAll();  // ✅ Método heredado
    }
}
```

**SQL generado:**
```sql
SELECT * FROM clientes;
```

### 2. Métodos Derivados (Query Methods)

Spring Data JPA genera queries automáticamente desde el nombre del método:

```java
@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Integer> {
    Optional<Clientes> findByEmail(String email);      // ✅ Spring genera la query
    boolean existsByTelefono(String telefono);          // ✅ Spring genera la query
}
```

**Reglas de nomenclatura:**
- `findBy` + `Campo`: Busca por campo
- `existsBy` + `Campo`: Verifica existencia
- `countBy` + `Campo`: Cuenta registros
- `deleteBy` + `Campo`: Elimina registros

**Ejemplo:**
```java
@Service
public class ClienteService {
    public Clientes findByEmailOrThrow(String email) {
        return clientesRepository.findByEmail(email.trim())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
}
```

**SQL generado:**
```sql
SELECT * FROM clientes WHERE email = 'test@gmail.com';
```

### 3. Métodos con @Query (JPQL Personalizado)

Para queries complejas, defines la query JPQL manualmente:

```java
@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Integer> {
    @Query("""
        SELECT c
        FROM Clientes c
        WHERE (:telefono IS NULL OR c.telefono LIKE CONCAT('%', :telefono, '%'))
          AND (:email IS NULL OR c.email LIKE CONCAT('%', :email, '%'))
        ORDER BY c.id
    """)
    List<Clientes> buscarClientesFiltrados(
            @Param("telefono") String telefono,
            @Param("email") String email);
}
```

**SQL generado (ejemplo):**
```sql
SELECT * FROM clientes
WHERE telefono LIKE '%600%'
  AND email LIKE '%@gmail.com%'
ORDER BY id_cliente;
```

## Mapeo de Entidades

### Anotaciones JPA Principales

```java
@Entity                    // Marca la clase como entidad JPA
@Table(name = "clientes")  // Nombre de la tabla en BD
public class Clientes {
    
    @Id                    // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento
    @Column(name = "id_cliente")  // Nombre de columna en BD
    private Integer id;
    
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.EAGER)  // Relación muchos-a-uno
    @JoinColumn(name = "id_empleado_responsable")
    private Empleados idEmpleadoResponsable;
}
```

### Tipos de Fetch

- **EAGER**: Carga la relación inmediatamente
  - **Ventaja**: Datos siempre disponibles
  - **Desventaja**: Puede causar N+1 queries

- **LAZY**: Carga la relación solo cuando se accede
  - **Ventaja**: Más eficiente, carga solo lo necesario
  - **Desventaja**: Puede causar `LazyInitializationException` si se accede fuera de transacción

## Ejemplo Real del Proyecto

### Flujo Completo: Listar Clientes

```
1. Controlador
   ┌─────────────────────────────────────────────┐
   │ @GetMapping                                 │
   │ public String listar(Model model) {         │
   │     model.addAttribute("clientes",          │
   │         clienteService.findAll());          │
   │     return "clientes/list";                 │
   │ }                                           │
   └─────────────────────────────────────────────┘
                       │
                       ▼
2. Servicio
   ┌─────────────────────────────────────────────┐
   │ public List<Clientes> findAll() {           │
   │     return clientesRepository.findAll();    │
   │ }                                           │
   └─────────────────────────────────────────────┘
                       │
                       ▼
3. Repositorio (Interface)
   ┌─────────────────────────────────────────────┐
   │ public interface ClientesRepository         │
   │     extends JpaRepository<Clientes, Integer>│
   │ {                                           │
   │     // findAll() está heredado              │
   │ }                                           │
   └─────────────────────────────────────────────┘
                       │
                       ▼
4. Spring Data JPA genera implementación
   ┌─────────────────────────────────────────────┐
   │ Ejecuta: SELECT * FROM clientes             │
   │ Mapea: ResultSet → List<Clientes>           │
   └─────────────────────────────────────────────┘
                       │
                       ▼
5. Retorna al servicio → controlador → vista
```

## Resumen

| Concepto | Explicación |
|----------|-------------|
| **JpaRepository** | Interface base que proporciona métodos CRUD |
| **Métodos heredados** | `findAll()`, `findById()`, `save()`, etc. - Ya implementados |
| **Métodos derivados** | Spring genera query desde el nombre (`findByEmail`) |
| **@Query** | Query JPQL personalizada para casos complejos |
| **@Entity** | Marca una clase como entidad JPA |
| **@Column** | Mapea campo Java a columna de BD |
| **@ManyToOne** | Define relación muchos-a-uno entre entidades |
| **FetchType.EAGER** | Carga relación inmediatamente |
| **FetchType.LAZY** | Carga relación solo cuando se accede |

## Ventajas de Spring Data JPA

1. **Menos código**: No necesitas implementar métodos CRUD básicos
2. **Queries automáticas**: Spring genera queries desde nombres de métodos
3. **Type-safe**: Errores en tiempo de compilación, no en runtime
4. **Flexible**: Puedes usar queries personalizadas cuando lo necesites
5. **Estándar JPA**: Usa el estándar JPA, no depende de Hibernate directamente

## Notas Importantes

1. **No implementas el repositorio**: Spring genera la implementación automáticamente en runtime.

2. **Nomenclatura de métodos derivados**: El nombre del método debe seguir el patrón `findBy` + `Campo` para que Spring genere la query.

3. **JPQL vs SQL**: Las queries en `@Query` usan JPQL (Java Persistence Query Language), no SQL directo. Hibernate las convierte a SQL.

4. **Transacciones**: Los métodos del repositorio se ejecutan dentro de transacciones. Usa `@Transactional` en servicios para controlar las transacciones.

5. **LazyInitializationException**: Si usas `FetchType.LAZY` y accedes a la relación fuera de una transacción, obtendrás este error. Solución: Usa `@Transactional` o cambia a `EAGER`.

6. **Parámetros en @Query**: Usa `@Param` para nombrar parámetros en queries JPQL.
