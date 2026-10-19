package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.dto.AsignaturaDto;
import co.edu.uts.tutorias.dto.UsuarioCrearDto;
import co.edu.uts.tutorias.dto.UsuarioRolDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Asignatura;
import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.service.AsignaturaService;
import co.edu.uts.tutorias.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de administración para gestión de asignaturas y usuarios.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AsignaturaService asignaturaService;
    private final UsuarioService usuarioService;

    public AdminController(AsignaturaService asignaturaService, UsuarioService usuarioService) {
        this.asignaturaService = asignaturaService;
        this.usuarioService = usuarioService;
    }

    // ==========================================
    // GESTIÓN DE ASIGNATURAS
    // ==========================================

    @GetMapping("/asignaturas")
    public String listarAsignaturas(Model model) {
        if (!model.containsAttribute("asignaturaDto")) {
            model.addAttribute("asignaturaDto", new AsignaturaDto());
        }
        model.addAttribute("asignaturas", asignaturaService.listarTodas());
        return "admin/asignaturas";
    }

    @PostMapping("/asignaturas")
    public String crearAsignatura(@Valid @ModelAttribute("asignaturaDto") AsignaturaDto dto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("asignaturas", asignaturaService.listarTodas());
            return "admin/asignaturas";
        }

        try {
            Asignatura nueva = asignaturaService.crear(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Asignatura '" + nueva.getNombre() + "' registrada con éxito.");
            return "redirect:/admin/asignaturas";
        } catch (ReglaNegocioException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("asignaturas", asignaturaService.listarTodas());
            return "admin/asignaturas";
        }
    }

    // ==========================================
    // GESTIÓN DE USUARIOS
    // ==========================================

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        prepararModeloUsuarios(model);
        if (!model.containsAttribute("usuarioCrearDto")) {
            model.addAttribute("usuarioCrearDto", new UsuarioCrearDto());
        }
        return "admin/usuarios";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(@Valid @ModelAttribute("usuarioCrearDto") UsuarioCrearDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            prepararModeloUsuarios(model);
            return "admin/usuarios";
        }

        try {
            Usuario nuevo = usuarioService.crear(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario '" + nuevo.getNombre() + "' registrado correctamente.");
            return "redirect:/admin/usuarios";
        } catch (ReglaNegocioException e) {
            model.addAttribute("mensajeError", e.getMessage());
            prepararModeloUsuarios(model);
            return "admin/usuarios";
        }
    }

    @PostMapping("/usuarios/{id}/rol")
    public String actualizarRolYAsignaturas(@PathVariable("id") String id,
                                           @Valid @ModelAttribute("usuarioRolDto") UsuarioRolDto dto,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Datos inválidos para actualizar el rol del usuario.");
            return "redirect:/admin/usuarios";
        }

        try {
            Usuario actualizado = usuarioService.actualizarRolYAsignaturas(id, dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Rol y asignaturas actualizados para el usuario: " + actualizado.getNombre());
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    private void prepararModeloUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Asignatura> asignaturas = asignaturaService.listarTodas();

        // Mapa de asignaturaId -> Nombre de la asignatura para renderizar rápido en la vista
        Map<String, String> mapaAsignaturas = new HashMap<>();
        for (Asignatura a : asignaturas) {
            mapaAsignaturas.put(a.getId(), a.getCodigo() + " - " + a.getNombre());
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("asignaturas", asignaturas);
        model.addAttribute("mapaAsignaturas", mapaAsignaturas);
        model.addAttribute("roles", Rol.values());
    }
}
