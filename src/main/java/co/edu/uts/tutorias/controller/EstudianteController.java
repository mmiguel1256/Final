package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.config.SesionUsuario;
import co.edu.uts.tutorias.dto.SolicitudCitaDto;
import co.edu.uts.tutorias.model.Asignatura;
import co.edu.uts.tutorias.model.Cita;
import co.edu.uts.tutorias.model.Disponibilidad;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.service.AsignaturaService;
import co.edu.uts.tutorias.service.CitaService;
import co.edu.uts.tutorias.service.DisponibilidadService;
import co.edu.uts.tutorias.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

/**
 * Controlador de cara al estudiante: búsqueda de franjas de tutoría y seguimiento de mis citas.
 */
@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    private final DisponibilidadService disponibilidadService;
    private final AsignaturaService asignaturaService;
    private final UsuarioService usuarioService;
    private final CitaService citaService;
    private final SesionUsuario sesionUsuario;

    public EstudianteController(DisponibilidadService disponibilidadService,
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

    @GetMapping("/tutorias")
    public String buscarTutorias(@RequestParam(value = "asignatura", required = false) String asignaturaId,
                                 Model model) {
        List<Asignatura> asignaturas = asignaturaService.listarTodas();
        List<Disponibilidad> franjasLibres;

        if (asignaturaId != null && !asignaturaId.trim().isEmpty()) {
            franjasLibres = disponibilidadService.listarLibresPorAsignatura(asignaturaId.trim());
        } else {
            franjasLibres = disponibilidadService.listarTodasLibres();
        }

        // Filtrar solo franjas con fecha de hoy o futura y ordenar cronológicamente
        List<Disponibilidad> franjasValidas = franjasLibres.stream()
                .filter(f -> !f.getFecha().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Disponibilidad::getFecha).thenComparing(Disponibilidad::getHoraInicio))
                .toList();

        // Mapas para renderizar rápidamente nombres de asignaturas y de docentes
        Map<String, Asignatura> mapaAsignaturas = new HashMap<>();
        for (Asignatura a : asignaturas) {
            mapaAsignaturas.put(a.getId(), a);
        }

        Map<String, Usuario> mapaDocentes = new HashMap<>();
        for (Usuario u : usuarioService.listarTodos()) {
            mapaDocentes.put(u.getId(), u);
        }

        model.addAttribute("asignaturas", asignaturas);
        model.addAttribute("asignaturaSeleccionada", asignaturaId);
        model.addAttribute("franjas", franjasValidas);
        model.addAttribute("mapaAsignaturas", mapaAsignaturas);
        model.addAttribute("mapaDocentes", mapaDocentes);
        model.addAttribute("solicitudCitaDto", new SolicitudCitaDto());

        return "estudiante/tutorias";
    }

    @GetMapping("/citas")
    public String misCitas(Model model) {
        String estudianteId = sesionUsuario.getId();
        List<Cita> citas = citaService.listarPorEstudiante(estudianteId);

        Map<String, Disponibilidad> mapaDisponibilidades = new HashMap<>();
        Map<String, Asignatura> mapaAsignaturas = new HashMap<>();
        Map<String, Usuario> mapaDocentes = new HashMap<>();

        for (Asignatura a : asignaturaService.listarTodas()) {
            mapaAsignaturas.put(a.getId(), a);
        }

        for (Usuario u : usuarioService.listarTodos()) {
            mapaDocentes.put(u.getId(), u);
        }

        for (Cita c : citas) {
            disponibilidadService.buscarPorId(c.getDisponibilidadId()).ifPresent(disp -> {
                mapaDisponibilidades.put(disp.getId(), disp);
            });
        }

        model.addAttribute("citas", citas);
        model.addAttribute("mapaDisponibilidades", mapaDisponibilidades);
        model.addAttribute("mapaAsignaturas", mapaAsignaturas);
        model.addAttribute("mapaDocentes", mapaDocentes);

        return "estudiante/citas";
    }
}
