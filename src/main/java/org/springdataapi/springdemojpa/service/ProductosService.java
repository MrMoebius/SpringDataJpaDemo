package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.models.ProductosDTO;
import org.springdataapi.springdemojpa.repository.ProductosRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class ProductosService {

    private final ProductosRepository productosRepository;

    public ProductosService(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }

    public List<Productos> findAll() {
        return productosRepository.findAll();
    }

    public Productos findById(Integer id) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        return productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public List<Productos> findByCategoria(String categoria) {
        String cat = normalizarYValidarCategoria(categoria);
        return productosRepository.findByCategoria(cat);
    }

    public List<Productos> findByActivo(Boolean activo) {
        if (activo == null) throw new RuntimeException("Activo obligatorio");
        return productosRepository.findByActivo(activo);
    }

    public List<Productos> findByPrecio(Double precio) {
        if (precio == null) throw new RuntimeException("Precio obligatorio");
        if (precio < 0) throw new RuntimeException("El precio no puede ser negativo");
        return productosRepository.findByPrecio(precio);
    }

    public void crear(ProductosDTO dto) {
        validarCampos(dto);

        String nombre = dto.getNombre().trim();
        if (productosRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Nombre ya registrado");
        }

        Productos p = new Productos();
        p.setNombre(nombre);
        p.setDescripcion(normalizarOptional(dto.getDescripcion()));
        p.setCategoria(dto.getCategoria()); // ya normalizada y validada
        p.setPrecio(dto.getPrecio());       // obligatorio y >= 0
        p.setActivo(dto.getActivo() == null ? Boolean.TRUE : dto.getActivo());

        productosRepository.save(p);
    }

    public Productos actualizar(Integer id, ProductosDTO dto) {
        if (id == null) throw new RuntimeException("Id obligatorio");

        Productos p = productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        validarCampos(dto);

        String nuevoNombre = dto.getNombre().trim();
        if (!p.getNombre().equals(nuevoNombre) && productosRepository.existsByNombre(nuevoNombre)) {
            throw new RuntimeException("Nombre ya registrado");
        }

        p.setNombre(nuevoNombre);
        p.setDescripcion(normalizarOptional(dto.getDescripcion()));
        p.setCategoria(dto.getCategoria()); // ya normalizada y validada
        p.setPrecio(dto.getPrecio());       // obligatorio y >= 0

        if (dto.getActivo() != null) {
            p.setActivo(dto.getActivo());
        }

        return productosRepository.save(p);
    }

    public void eliminar(Integer id) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        if (!productosRepository.existsById(id)) throw new RuntimeException("Producto no existe");
        try {
            productosRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar el producto porque tiene registros relacionados (facturas, presupuestos, etc.)");
        }
    }

    private void validarCampos(ProductosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("Nombre obligatorio");
        }

        if (dto.getPrecio() == null) {
            throw new RuntimeException("Precio obligatorio");
        }

        if (dto.getPrecio() < 0) {
            throw new RuntimeException("¿Desde cuando el precio de un producto es negativo bobolón?, pon otro");
        }

        dto.setCategoria(normalizarYValidarCategoria(dto.getCategoria()));
    }

    /**
     * Exige SOLO estas categorías:
     * - CICLO FORMATIVO
     * - FORMACION COMPLEMENTARIA
     *
     * Acepta minúsculas, tildes y espacios extra (se normaliza).
     */
    private String normalizarYValidarCategoria(String categoria) {
        if (categoria == null) throw new RuntimeException("Categoría obligatoria");

        String c = categoria.trim();
        if (c.isBlank()) throw new RuntimeException("Categoría obligatoria");

        c = c.replaceAll("\\s+", " ");
        c = Normalizer.normalize(c, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        c = c.toUpperCase();

        if (c.equals("CICLO FORMATIVO")) return "CICLO FORMATIVO";
        if (c.equals("FORMACION COMPLEMENTARIA")) return "FORMACION COMPLEMENTARIA";

        throw new IllegalArgumentException(
                "Categoría inválida: " + categoria +
                        ". Valores válidos: CICLO FORMATIVO, FORMACION COMPLEMENTARIA"
        );
    }

    private String normalizarOptional(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
