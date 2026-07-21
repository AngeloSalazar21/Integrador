package com.kenpaku.ferreteria.controller;

import com.kenpaku.ferreteria.config.LoginAttemptService;
import com.kenpaku.ferreteria.dto.ProductoDTO;
import com.kenpaku.ferreteria.service.CategoriaService;
import com.kenpaku.ferreteria.service.MarcaService;
import com.kenpaku.ferreteria.service.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final LoginAttemptService loginAttemptService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;

    public HomeController(LoginAttemptService loginAttemptService,
                          ProductoService productoService,
                          CategoriaService categoriaService,
                          MarcaService marcaService) {
        this.loginAttemptService = loginAttemptService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        var productos = productoService.obtenerTodos();
        var categorias = categoriaService.obtenerTodas();
        var marcas = marcaService.obtenerTodas();

        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("totalCategorias", categorias.size());
        model.addAttribute("totalMarcas", marcas.size());

        // Datos para gráfico de productos por categoría
        var productosPorCategoria = productos.stream()
                .collect(Collectors.groupingBy(ProductoDTO::getCategoriaId, Collectors.counting()));
        model.addAttribute("categoriaLabels", categorias.stream().map(c -> c.getNombre()).collect(Collectors.toList()));
        model.addAttribute("categoriaData", categorias.stream()
                .map(c -> productosPorCategoria.getOrDefault(c.getId(), 0L))
                .collect(Collectors.toList()));

        // Datos para gráfico de productos por marca
        var productosPorMarca = productos.stream()
                .collect(Collectors.groupingBy(ProductoDTO::getMarcaId, Collectors.counting()));
        model.addAttribute("marcaLabels", marcas.stream().map(m -> m.getNombre()).collect(Collectors.toList()));
        model.addAttribute("marcaData", marcas.stream()
                .map(m -> productosPorMarca.getOrDefault(m.getId(), 0L))
                .collect(Collectors.toList()));

        // Datos para gráfico de stock
        var topStock = productos.stream()
                .sorted((a, b) -> Long.compare(b.getStock(), a.getStock()))
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("stockLabels", topStock.stream().map(p -> p.getNombre()).collect(Collectors.toList()));
        model.addAttribute("stockData", topStock.stream().map(p -> p.getStock()).collect(Collectors.toList()));

        return "index";
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String locked,
            @RequestParam(required = false) String reset,
            HttpServletRequest request,
            Model model) {

        if (reset != null) {
            loginAttemptService.reset(request);
        }

        boolean lockedState = loginAttemptService.isLocked(request) || locked != null;
        int attempts = loginAttemptService.getFailedAttempts(request);
        int remainingAttempts = Math.max(0, LoginAttemptService.MAX_ATTEMPTS - attempts);

        model.addAttribute("error", error != null && !lockedState);
        model.addAttribute("logout", logout != null);
        model.addAttribute("locked", lockedState);
        model.addAttribute("remainingAttempts", remainingAttempts);
        model.addAttribute("resetUrl", "/login?reset=true");

        return "login";
    }
}
