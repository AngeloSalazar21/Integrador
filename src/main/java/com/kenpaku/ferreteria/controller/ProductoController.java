package com.kenpaku.ferreteria.controller;

import com.kenpaku.ferreteria.dto.ProductoDTO;
import com.kenpaku.ferreteria.service.CategoriaService;
import com.kenpaku.ferreteria.service.MarcaService;
import com.kenpaku.ferreteria.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductoController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              MarcaService marcaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
    }

    @GetMapping("/productos")
    public String listarProductos(@RequestParam(required = false) Long categoriaId,
                                  @RequestParam(required = false) Long marcaId,
                                  @RequestParam(required = false) Boolean activo,
                                  Model model) {
        model.addAttribute("productos", productoService.obtenerPorFiltros(categoriaId, marcaId, activo));
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("marcas", marcaService.obtenerTodas());
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("marcaId", marcaId);
        model.addAttribute("activo", activo);
        return "productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProductoFormulario(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("marcas", marcaService.obtenerTodas());
        return "producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") ProductoDTO productoDTO,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.obtenerTodas());
            model.addAttribute("marcas", marcaService.obtenerTodas());
            return "producto-form";
        }

        if (productoDTO.getId() != null) {
            productoService.actualizar(productoDTO.getId(), productoDTO);
        } else {
            productoService.crear(productoDTO);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Producto guardado exitosamente");
        return "redirect:/productos";
    }

    @GetMapping("/productos/{id}/editar")
    public String editarProductoFormulario(@PathVariable Long id, Model model) {
        ProductoDTO producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("marcas", marcaService.obtenerTodas());
        return "producto-form";
    }

    @PostMapping("/productos/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto desactivado exitosamente");
        return "redirect:/productos";
    }

    @GetMapping("/api/productos/{id}")
    @ResponseBody
    public ProductoDTO obtenerProductoAPI(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }
}
