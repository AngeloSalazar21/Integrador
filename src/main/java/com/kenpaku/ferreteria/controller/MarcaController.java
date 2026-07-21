package com.kenpaku.ferreteria.controller;

import com.kenpaku.ferreteria.dto.MarcaDTO;
import com.kenpaku.ferreteria.service.MarcaService;
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
public class MarcaController {
    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @GetMapping("/marcas")
    public String listarMarcas(Model model) {
        model.addAttribute("marcas", marcaService.obtenerTodas());
        return "marcas";
    }

    @GetMapping("/marcas/nuevo")
    public String nuevoMarcaFormulario(Model model) {
        model.addAttribute("marca", new MarcaDTO());
        return "marca-form";
    }

    @PostMapping("/marcas/guardar")
    public String guardarMarca(@Valid @ModelAttribute("marca") MarcaDTO marcaDTO,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "marca-form";
        }

        if (marcaDTO.getId() != null) {
            marcaService.actualizar(marcaDTO.getId(), marcaDTO);
        } else {
            marcaService.crear(marcaDTO);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Marca guardada exitosamente");
        return "redirect:/marcas";
    }

    @GetMapping("/marcas/{id}/editar")
    public String editarMarcaFormulario(@PathVariable Long id, Model model) {
        MarcaDTO marca = marcaService.obtenerPorId(id);
        model.addAttribute("marca", marca);
        return "marca-form";
    }

    @PostMapping("/marcas/{id}/eliminar")
    public String eliminarMarca(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        marcaService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Marca desactivada exitosamente");
        return "redirect:/marcas";
    }

    @GetMapping("/api/marcas/{id}")
    @ResponseBody
    public MarcaDTO obtenerMarcaAPI(@PathVariable Long id) {
        return marcaService.obtenerPorId(id);
    }
}
