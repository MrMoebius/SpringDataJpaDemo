package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.models.ProductosDTO;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductosController {

    private final ProductosService productosService;

    public ProductosController(ProductosService productosService) {
        this.productosService = productosService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productosService.findAll());
        return "productos/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        ProductosDTO dto = new ProductosDTO();
        dto.setActivo(true);
        model.addAttribute("productosDTO", dto);
        return "productos/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Productos p = productosService.findById(id);

        ProductosDTO dto = new ProductosDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setCategoria(p.getCategoria());
        dto.setPrecio(p.getPrecio());
        dto.setActivo(p.getActivo());

        model.addAttribute("productosDTO", dto);
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("productosDTO") ProductosDTO dto,
                          BindingResult br,
                          Model model) {
        if (br.hasErrors()) return "productos/form";

        try {
            if (dto.getId() == null) productosService.crear(dto);
            else productosService.actualizar(dto.getId(), dto);
            return "redirect:/productos";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "productos/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productosService.eliminar(id);
        return "redirect:/productos";
    }

    @GetMapping("/precio")
    public String listarPorPrecio(@RequestParam("valor") Double valor, Model model) {
        model.addAttribute("productos", productosService.findByPrecio(valor));
        return "productos/list";
    }

}
