package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.models.ProductosDTO;
import org.springdataapi.springdemojpa.repository.ProductosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<Productos> findProductosActivos() {
        return productosRepository.findByActivo(true);
    }

    public Productos findById(Integer id) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        return productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public List<Productos> findByCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) throw new RuntimeException("Categoría obligatoria");
        return productosRepository.findByCategoria(categoria.trim());
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
        validarCamposCrear(dto);

        String nombre = dto.getNombre().trim();
        if (productosRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Nombre ya registrado");
        }

        Productos p = new Productos();
        p.setNombre(nombre);
        p.setDescripcion(normalizarOptional(dto.getDescripcion()));
        p.setCategoria(normalizarOptional(dto.getCategoria()));

        if (dto.getPrecio() != null && dto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        p.setPrecio(dto.getPrecio());

        p.setActivo(dto.getActivo() == null ? Boolean.TRUE : dto.getActivo());

        productosRepository.save(p);
    }

    public Productos actualizar(Integer id, ProductosDTO dto) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        validarCamposActualizar(dto);

        Productos p = productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        String nuevoNombre = dto.getNombre().trim();
        if (!p.getNombre().equals(nuevoNombre) && productosRepository.existsByNombre(nuevoNombre)) {
            throw new RuntimeException("Nombre ya registrado");
        }

        if (dto.getPrecio() != null && dto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }

        p.setNombre(nuevoNombre);
        p.setDescripcion(normalizarOptional(dto.getDescripcion()));
        p.setCategoria(normalizarOptional(dto.getCategoria()));
        p.setPrecio(dto.getPrecio());

        if (dto.getActivo() != null) {
            p.setActivo(dto.getActivo());
        }

        return productosRepository.save(p);
    }

    public void eliminar(Integer id) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        if (!productosRepository.existsById(id)) throw new RuntimeException("Producto no existe");
        productosRepository.deleteById(id);
    }

    public List<Productos> BuscarProductosFiltrados(String categoria, Double precioMin)
    {
        return productosRepository.buscarProductosFiltrados(categoria,precioMin);
    }

    private void validarCamposCrear(ProductosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("Nombre obligatorio");
        }
    }

    private void validarCamposActualizar(ProductosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("Nombre obligatorio");
        }
    }

    private String normalizarOptional(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}

