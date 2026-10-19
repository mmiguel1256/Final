package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.config.SesionUsuario;
import co.edu.uts.tutorias.dto.DisponibilidadDto;
import co.edu.uts.tutorias.dto.RespuestaSolicitudDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Asignatura;
import co.edu.uts.tutorias.model.Cita;
import co.edu.uts.tutorias.model.Disponibilidad;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.service.AsignaturaService;
import co.edu.uts.tutorias.service.CitaService;
import co.edu.uts.tutorias.service.DisponibilidadService;
import co.edu.uts.tutorias.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Controlador para docentes y monitores: gestión de solicitudes y publicación de disponibilidad.
 */
@Controller
@RequestMapping("/docente")
public class DocenteController {

    private final DisponibilidadService disponibilidadService;
    private final AsignaturaService asignaturaService;
    private final UsuarioService usuarioService;
    private final CitaService citaService;
    private final SesionUsuario sesionUsuario;

    public DocenteController(DisponibilidadService disponibilidadService,
                             AsignaturaService asignaturaService,
                             UsuarioService usuarioService,
                             CitaService citaService,
                             SesionUsuario sesionUsuario) {
        this.disponibilidadService = disponibilidadService;
        this.asignaturaService = asignaturaService;
        this.usuarioService = usuarioService;
        this.citaService = citaService;
        this.sesionUsuario = sesionUsuario;
    }

    @GetMapping("/solicitudes")
    public String bandejaSolicitudes(Model model) {
        String docenteId = sesionUsuario.getId();
        List<Cita> solicitudes = citaService.listarSolicitudesPorDocente(docenteId);

        Map<String, Disponibilidad> mapaDisponibilidades = new HashMap<>();
        Map<String, Asignatura> mapaAsignaturas = new HashMap<>();
        Map<String, Usuario> mapaEstudiantes = new HashMap<>();

        for (Asignatura a : asignaturaService.listarTodas()) {
            mapaAsignaturas.put(a.getId(), a);
        }

        for (Usuario u : usuarioService.listarTodos()) {
            mapaEstudiantes.put(u.getId(), u);
        }

        for (Cita c : solicitudes) {
            disponibilidadService.buscarPorId(c.getDisponibilidadId()).ifPresent(d -> {
                mapaDisponibilidades.put(d.getId(), d);
            });
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("mapaDisponibilidades", mapaDisponibilidades);
        model.addAttribute("mapaAsignaturas", mapaAsignaturas);
        model.addAttribute("mapaEstudiantes", mapaEstudiantes);
        model.addAttribute("respuestaDto", new RespuestaSolicitudDto());

        return "docente/solicitudes";
    }

    @GetMapping("/disponibilidad")
    public String disponibilidad(Model model) {
        prepararModeloDisponibilidad(model);
        if (!model.containsAttribute("disponibilidadDto")) {
            model.addAttribute("disponibilidadDto", new DisponibilidadDto());
        }
        return "docente/disponibilidad";
    }

    @PostMapping("/disponibilidad")
    public String publicarDisponibilidad(@Valid @ModelAttribute("disponibilidadDto") DisponibilidadDto dto,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            prepararModeloDisponibilidad(model);
            return "docente/disponibilidad";
        }

        try {
            disponibilidadService.crearFranja(sesionUsuario.getId(), dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Franja de tutoría publicada correctamente.");
            return "redirect:/docente/disponibilidad";
        } catch (ReglaNegocioException e) {
            model.addAttribute("mensajeError", e.getMessage());
            prepararModeloDisponibilidad(model);
            return "docente/disponibilidad";
        }
    }

    @PostMapping("/disponibilidad/{id}/eliminar")
    public String eliminarDisponibilidad(@PathVariable("id") String id,
                                         RedirectAttributes redirectAttributes) {
        try {
            disponibilidadService.eliminarFranja(sesionUsuario.getId(), id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Franja horaria eliminada con éxito.");
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/docente/disponibilidad";
    }

    private void prepararModeloDisponibilidad(Model model) {
        String docenteId = sesionUsuario.getId();
        List<Disponibilidad> franjas = disponibilidadService.listarPorDocente(docenteId);

        Usuario docente = usuarioService.buscarPorId(docenteId).orElse(null);
        List<Asignatura> asignaturasAsignadas = new ArrayList<>();
        Map<String, String> mapaAsignaturas = new HashMap<>();

        if (docente != null && docente.getAsignaturasIds() != null) {
            for (String asigId : docente.getAsignaturasIds()) {
                asignaturaService.buscarPorId(asigId).ifPresent(asig -> {
                    asignaturasAsignadas.add(asig);
                    mapaAsignaturas.put(asig.getId(), asig.getCodigo() + " - " + asig.getNombre());
                });
            }
        }

        model.addAttribute("franjas", franjas);
        model.addAttribute("asignaturasAsignadas", asignaturasAsignadas);
        model.addAttribute("mapaAsignaturas", mapaAsignaturas);
    }
}
