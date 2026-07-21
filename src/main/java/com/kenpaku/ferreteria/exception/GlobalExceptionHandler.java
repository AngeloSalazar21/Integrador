package com.kenpaku.ferreteria.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Manejador centralizado de excepciones para toda la aplicación.
 *
 * <p>Convierte las excepciones no controladas en respuestas HTTP coherentes y
 * registra el detalle en el log, evitando que el usuario vea trazas de error
 * crudas (páginas 500 sin contexto).</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Recurso inexistente (producto, categoría o marca no encontrada).
     *
     * @param ex    excepción capturada
     * @param model modelo para la vista de error
     * @return vista {@code error} con estado HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        model.addAttribute("codigo", 404);
        model.addAttribute("titulo", "Recurso no encontrado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    /**
     * Error de reglas de negocio / validación manual.
     *
     * @param ex    excepción capturada
     * @param model modelo para la vista de error
     * @return vista {@code error} con estado HTTP 400
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(ValidationException ex, Model model) {
        log.warn("Error de validación: {}", ex.getMessage());
        model.addAttribute("codigo", 400);
        model.addAttribute("titulo", "Datos inválidos");
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    /**
     * Cualquier otro error no previsto.
     *
     * @param ex    excepción capturada
     * @param model modelo para la vista de error
     * @return vista {@code error} con estado HTTP 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception ex, Model model) {
        log.error("Error interno no controlado", ex);
        model.addAttribute("codigo", 500);
        model.addAttribute("titulo", "Error interno del servidor");
        model.addAttribute("mensaje", "Ocurrió un error inesperado. Intenta nuevamente más tarde.");
        return "error";
    }
}
