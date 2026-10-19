package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.config.SesionUsuario;
import co.edu.uts.tutorias.dto.LoginDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para las rutas públicas de acceso: inicio, login, logout y acceso denegado.
 */
@Controller
public class AccesoController {

    private final UsuarioService usuarioService;
    private final SesionUsuario sesionUsuario;

    public AccesoController(UsuarioService usuarioService, SesionUsuario sesionUsuario) {
        this.usuarioService = usuarioService;
        this.sesionUsuario = sesionUsuario;
    }

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        if (sesionUsuario.estaAutenticado()) {
            return "redirect:" + obtenerRutaPanelSegunRol(sesionUsuario.getRol());
        }
        if (!model.containsAttribute("loginDto")) {
            model.addAttribute("loginDto", new LoginDto());
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            Usuario usuario = usuarioService.autenticarPorCorreo(loginDto.getCorreo());
            sesionUsuario.iniciar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Bienvenido, " + usuario.getNombre() + "!");
            return "redirect:" + obtenerRutaPanelSegunRol(usuario.getRol());
        } catch (ReglaNegocioException e) {
            model.addAttribute("mensajeError", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(RedirectAttributes redirectAttributes) {
        sesionUsuario.cerrar();
        redirectAttributes.addFlashAttribute("mensajeExito", "Ha cerrado sesión correctamente.");
        return "redirect:/";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    /**
     * Determina la ruta del panel inicial según el rol del usuario (Sección 3 de la especificación).
     */
    private String obtenerRutaPanelSegunRol(Rol rol) {
        if (rol == null) {
            return "/";
        }
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/tutorias";
            case DOCENTE, MONITOR -> "/docente/solicitudes";
            case COORDINADOR, DIRECTOR -> "/reportes/asignaturas";
            case BIENESTAR -> "/reportes/participacion";
            case ADMIN -> "/admin/asignaturas";
        };
    }
}
