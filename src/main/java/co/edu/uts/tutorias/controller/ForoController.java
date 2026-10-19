package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.config.SesionUsuario;
import co.edu.uts.tutorias.dto.PublicacionDto;
import co.edu.uts.tutorias.dto.RespuestaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Asignatura;
import co.edu.uts.tutorias.model.Publicacion;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.service.AsignaturaService;
import co.edu.uts.tutorias.service.ForoService;
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
 * Controlador para los foros colaborativos por asignatura.
 * Acceso permitido a ESTUDIANTE, DOCENTE y MONITOR.
 */
@Controller
@RequestMapping("/foro")
public class ForoController {

    private final ForoService foroService;
    private final AsignaturaService asignaturaService;
    private final UsuarioService usuarioService;
    private final SesionUsuario sesionUsuario;

    public ForoController(ForoService foroService,
                          AsignaturaService asignaturaService,
                          UsuarioService usuarioService,
                          SesionUsuario sesionUsuario) {
        this.foroService = foroService;
        this.asignaturaService = asignaturaService;
        this.usuarioService = usuarioService;
        this.sesionUsuario = sesionUsuario;
    }

    /**
     * Catálogo general de salas de foro agrupadas por asignatura.
     * GET /foro
     */
    @GetMapping
    public String listarSalas(Model model) {
        List<Asignatura> asignaturas = asignaturaService.listarTodas();

        Map<String, Long> conteoPublicaciones = new HashMap<>();
        for (Asignatura a : asignaturas) {
            conteoPublicaciones.put(a.getId(), foroService.contarPorAsignatura(a.getId()));
        }

        model.addAttribute("asignaturas", asignaturas);
        model.addAttribute("conteoPublicaciones", conteoPublicaciones);

        return "foro/lista";
    }

    /**
     * Sala de discusión de una asignatura específica.
     * GET /foro/{asignaturaId}
     */
    @GetMapping("/{asignaturaId}")
    public String verSala(@PathVariable("asignaturaId") String asignaturaId, Model model) {
        Asignatura asignatura = asignaturaService.buscarPorId(asignaturaId)
                .orElseThrow(() -> new ReglaNegocioException("La asignatura solicitada no existe."));

        prepararModeloSala(asignatura, model);
        if (!model.containsAttribute("publicacionDto")) {
            model.addAttribute("publicacionDto", new PublicacionDto());
        }
        if (!model.containsAttribute("respuestaDto")) {
            model.addAttribute("respuestaDto", new RespuestaDto());
        }

        return "foro/sala";
    }

    /**
     * Crear una nueva publicación en el foro.
     * POST /foro/{asignaturaId}/publicaciones
     */
    @PostMapping("/{asignaturaId}/publicaciones")
    public String crearPublicacion(@PathVariable("asignaturaId") String asignaturaId,
                                   @Valid @ModelAttribute("publicacionDto") PublicacionDto dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        Asignatura asignatura = asignaturaService.buscarPorId(asignaturaId)
                .orElseThrow(() -> new ReglaNegocioException("La asignatura no existe."));

        if (bindingResult.hasErrors()) {
            prepararModeloSala(asignatura, model);
            if (!model.containsAttribute("respuestaDto")) {
                model.addAttribute("respuestaDto", new RespuestaDto());
            }
            return "foro/sala";
        }

        try {
            foroService.crearPublicacion(asignaturaId, sesionUsuario.getId(), dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Duda o tema publicado exitosamente en el foro!");
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/foro/" + asignaturaId;
    }

    /**
     * Responder a una publicación existente.
     * POST /foro/{asignaturaId}/publicaciones/{publicacionId}/respuestas
     */
    @PostMapping("/{asignaturaId}/publicaciones/{publicacionId}/respuestas")
    public String agregarRespuesta(@PathVariable("asignaturaId") String asignaturaId,
                                   @PathVariable("publicacionId") String publicacionId,
                                   @Valid @ModelAttribute("respuestaDto") RespuestaDto dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensajeError", "La respuesta debe tener entre 2 y 500 caracteres.");
            return "redirect:/foro/" + asignaturaId;
        }

        try {
            foroService.agregarRespuesta(publicacionId, sesionUsuario.getId(), dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Respuesta enviada correctamente.");
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/foro/" + asignaturaId;
    }

    private void prepararModeloSala(Asignatura asignatura, Model model) {
        List<Publicacion> publicaciones = foroService.listarPorAsignatura(asignatura.getId());

        Map<String, Usuario> mapaUsuarios = new HashMap<>();
        for (Usuario u : usuarioService.listarTodos()) {
            mapaUsuarios.put(u.getId(), u);
        }

        model.addAttribute("asignatura", asignatura);
        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("mapaUsuarios", mapaUsuarios);
    }
}
