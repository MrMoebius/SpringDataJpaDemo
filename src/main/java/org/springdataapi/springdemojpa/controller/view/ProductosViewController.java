package org.springdataapi.springdemojpa.controller.view;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.models.ProductosDTO;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductosViewController {

    private final ProductosService productosService;

    public ProductosViewController(ProductosService productosService) {
        this.productosService = productosService;
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        if (authentication != null &&
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            model.addAttribute("productos", productosService.findProductosActivos());
            model.addAttribute("soloLectura", true);
        } else {
            model.addAttribute("productos", productosService.findAll());
            model.addAttribute("soloLectura", false);
        }
        return "productos/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String nuevo(Model model) {
        ProductosDTO dto = new ProductosDTO();
        dto.setActivo(true);
        model.addAttribute("productosDTO", dto);
        return "productos/form";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String guardar(@Valid @ModelAttribute("productosDTO") ProductosDTO dto,
                          BindingResult br, Model model, RedirectAttributes redirectAttributes) {
        if (br.hasErrors()) return "productos/form";
        try {
            if (dto.getId() == null) {
                productosService.crear(dto);
                redirectAttributes.addFlashAttribute("success", "Producto añadido correctamente");
            } else {
                productosService.actualizar(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("success", "Producto modificado correctamente");
            }
            return "redirect:/productos";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "productos/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String eliminar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            productosService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productos", productosService.findAll());
            return "productos/list";
        }
        return "redirect:/productos";
    }
}
