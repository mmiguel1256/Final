package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.config.SesionUsuario;
import co.edu.uts.tutorias.dto.EstudianteRiesgoDto;
import co.edu.uts.tutorias.dto.ReporteAsignaturaEstadoDto;
import co.edu.uts.tutorias.dto.ReporteDocenteDto;
import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controlador para la visualización de reportes académicos e indicadores de participación.
 * Respeta el principio de solo lectura y las restricciones de acceso por rol y programa.
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final SesionUsuario sesionUsuario;

    public ReporteController(ReporteService reporteService, SesionUsuario sesionUsuario) {
        this.reporteService = reporteService;
        this.sesionUsuario = sesionUsuario;
    }

    /**
     * Reportes por asignatura y por docente.
     * Permitido para: COORDINADOR, DIRECTOR, ADMIN.
     */
    @GetMapping("/asignaturas")
    public String reportesAsignaturas(Model model) {
        Rol rol = sesionUsuario.getRol();
        String programa = sesionUsuario.getPrograma();

        List<ReporteAsignaturaEstadoDto> reporteAsignaturas = reporteService.obtenerReportePorAsignaturaYEstado(rol, programa);
        List<ReporteDocenteDto> reporteDocentes = reporteService.obtenerReportePorDocente(rol, programa);

        model.addAttribute("reporteAsignaturas", reporteAsignaturas);
        model.addAttribute("reporteDocentes", reporteDocentes);
        model.addAttribute("esDirector", rol == Rol.DIRECTOR);
        model.addAttribute("programaDirector", programa);

        return "reportes/asignaturas";
    }

    /**
     * Indicadores de participación y estudiantes en riesgo en materias críticas.
     * Permitido para: BIENESTAR, ADMIN.
     */
    @GetMapping("/participacion")
    public String reportesParticipacion(Model model) {
        Rol rol = sesionUsuario.getRol();
        String programa = sesionUsuario.getPrograma();

        List<EstudianteRiesgoDto> estudiantesSinCitas = reporteService.obtenerEstudiantesSinCitasEnCriticas(rol, programa);

        model.addAttribute("estudiantesSinCitas", estudiantesSinCitas);
        model.addAttribute("totalEstudiantesRiesgo", estudiantesSinCitas.size());

        return "reportes/participacion";
    }
}
