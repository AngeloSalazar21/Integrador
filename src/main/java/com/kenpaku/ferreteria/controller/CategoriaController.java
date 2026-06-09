package com.kenpaku.ferreteria.controller;

import com.kenpaku.ferreteria.dto.CategoriaDTO;
import com.kenpaku.ferreteria.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        return "categorias";
    }

    @GetMapping("/categorias/nuevo")
    public String nuevoCategoriaFormulario(Model model) {
        model.addAttribute("categoria", new CategoriaDTO());
        return "categoria-form";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@Valid @ModelAttribute("categoria") CategoriaDTO categoriaDTO,
                                   BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categoria-form";
        }

        if (categoriaDTO.getId() != null) {
            categoriaService.actualizar(categoriaDTO.getId(), categoriaDTO);
        } else {
            categoriaService.crear(categoriaDTO);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Categoría guardada exitosamente");
        return "redirect:/categorias";
    }

    @GetMapping("/categorias/{id}/editar")
    public String editarCategoriaFormulario(@PathVariable Long id, Model model) {
        CategoriaDTO categoria = categoriaService.obtenerPorId(id);
        model.addAttribute("categoria", categoria);
        return "categoria-form";
    }

    @PostMapping("/categorias/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoriaService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Categoría desactivada exitosamente");
        return "redirect:/categorias";
    }

    @GetMapping("/api/categorias/{id}")
    @ResponseBody
    public CategoriaDTO obtenerCategoriaAPI(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id);
    }
}
