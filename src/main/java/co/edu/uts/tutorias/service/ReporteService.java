package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.EstudianteRiesgoDto;
import co.edu.uts.tutorias.dto.ReporteAsignaturaEstadoDto;
import co.edu.uts.tutorias.dto.ReporteDocenteDto;
import co.edu.uts.tutorias.model.*;
import co.edu.uts.tutorias.repository.AsignaturaRepository;
import co.edu.uts.tutorias.repository.CitaRepository;
import co.edu.uts.tutorias.repository.DisponibilidadRepository;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de reportería académica y participación estudiantil.
 * Cumple la restricción técnica de calcular métricas mediante listas y streams de Java (sin agregaciones de Mongo).
 */
@Service
public class ReporteService {

    private final AsignaturaRepository asignaturaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(AsignaturaRepository asignaturaRepository,
                          DisponibilidadRepository disponibilidadRepository,
                          CitaRepository citaRepository,
                          UsuarioRepository usuarioRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Reporte 1: Citas por asignatura y por estado.
     * Si el rol es DIRECTOR, solo se incluyen asignaturas de su programa académico.
     */
    public List<ReporteAsignaturaEstadoDto> obtenerReportePorAsignaturaYEstado(Rol rol, String programaUsuario) {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        List<Disponibilidad> disponibilidades = disponibilidadRepository.findAll();
        List<Cita> citas = citaRepository.findAll();

        // Mapeo de disponibilidadId -> asignaturaId
        Map<String, String> disponibilidadAAsignatura = disponibilidades.stream()
                .filter(d -> d.getId() != null && d.getAsignaturaId() != null)
                .collect(Collectors.toMap(Disponibilidad::getId, Disponibilidad::getAsignaturaId, (a, b) -> a));

        // Filtrar asignaturas si es DIRECTOR
        List<Asignatura> asignaturasFiltradas = asignaturas.stream()
                .filter(a -> {
                    if (rol == Rol.DIRECTOR && programaUsuario != null) {
                        return programaUsuario.equalsIgnoreCase(a.getPrograma());
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Asignatura::getNombre))
                .toList();

        List<ReporteAsignaturaEstadoDto> resultado = new ArrayList<>();

        for (Asignatura asig : asignaturasFiltradas) {
            // Citas asociadas a esta asignatura
            List<Cita> citasAsignatura = citas.stream()
                    .filter(c -> asig.getId().equals(disponibilidadAAsignatura.get(c.getDisponibilidadId())))
                    .toList();

            long total = citasAsignatura.size();
            long pendientes = citasAsignatura.stream().filter(c -> c.getEstado() == EstadoCita.PENDIENTE).count();
            long aceptadas = citasAsignatura.stream().filter(c -> c.getEstado() == EstadoCita.ACEPTADA).count();
            long rechazadas = citasAsignatura.stream().filter(c -> c.getEstado() == EstadoCita.RECHAZADA).count();
            long canceladas = citasAsignatura.stream().filter(c -> c.getEstado() == EstadoCita.CANCELADA).count();

            resultado.add(new ReporteAsignaturaEstadoDto(
                    asig.getCodigo(),
                    asig.getNombre(),
                    asig.getPrograma(),
                    asig.isCritica(),
                    total,
                    pendientes,
                    aceptadas,
                    rechazadas,
                    canceladas
            ));
        }

        return resultado;
    }

    /**
     * Reporte 2: Citas y franjas por docente y monitor.
     * Si el rol es DIRECTOR, filtra por el programa del usuario.
     */
    public List<ReporteDocenteDto> obtenerReportePorDocente(Rol rol, String programaUsuario) {
        List<Usuario> tutores = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == Rol.DOCENTE || u.getRol() == Rol.MONITOR)
                .filter(u -> {
                    if (rol == Rol.DIRECTOR && programaUsuario != null) {
                        return programaUsuario.equalsIgnoreCase(u.getPrograma());
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Usuario::getNombre))
                .toList();

        List<Disponibilidad> disponibilidades = disponibilidadRepository.findAll();
        List<Cita> citas = citaRepository.findAll();

        // Mapeo de disponibilidadId -> docenteId
        Map<String, String> dispADocente = disponibilidades.stream()
                .filter(d -> d.getId() != null && d.getDocenteId() != null)
                .collect(Collectors.toMap(Disponibilidad::getId, Disponibilidad::getDocenteId, (a, b) -> a));

        List<ReporteDocenteDto> resultado = new ArrayList<>();

        for (Usuario tutor : tutores) {
            List<Disponibilidad> franjasTutor = disponibilidades.stream()
                    .filter(d -> tutor.getId().equals(d.getDocenteId()))
                    .toList();

            long totalFranjas = franjasTutor.size();
            long libres = franjasTutor.stream().filter(d -> d.getEstado() == EstadoDisponibilidad.LIBRE).count();
            long reservadas = franjasTutor.stream().filter(d -> d.getEstado() == EstadoDisponibilidad.RESERVADA).count();

            List<Cita> citasTutor = citas.stream()
                    .filter(c -> tutor.getId().equals(dispADocente.get(c.getDisponibilidadId())))
                    .toList();

            long aceptadas = citasTutor.stream().filter(c -> c.getEstado() == EstadoCita.ACEPTADA).count();
            long rechazadas = citasTutor.stream().filter(c -> c.getEstado() == EstadoCita.RECHAZADA).count();

            resultado.add(new ReporteDocenteDto(
                    tutor.getNombre(),
                    tutor.getCorreo(),
                    tutor.getRol().name(),
                    tutor.getPrograma(),
                    totalFranjas,
                    libres,
                    reservadas,
                    aceptadas,
                    rechazadas
            ));
        }

        return resultado;
    }

    /**
     * Reporte 3: Estudiantes activos sin ninguna cita en asignaturas críticas.
     * Utilizado para Bienestar y Dirección. Cumple estrictamente con no mostrar tema ni comentarios.
     */
    public List<EstudianteRiesgoDto> obtenerEstudiantesSinCitasEnCriticas(Rol rol, String programaUsuario) {
        // 1. Obtener asignaturas críticas (filtradas por programa si es DIRECTOR)
        Set<String> asignaturasCriticasIds = asignaturaRepository.findByCriticaTrue().stream()
                .filter(a -> {
                    if (rol == Rol.DIRECTOR && programaUsuario != null) {
                        return programaUsuario.equalsIgnoreCase(a.getPrograma());
                    }
                    return true;
                })
                .map(Asignatura::getId)
                .collect(Collectors.toSet());

        if (asignaturasCriticasIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Obtener disponibilidades asociadas a asignaturas críticas
        Set<String> disponibilidadesCriticasIds = disponibilidadRepository.findAll().stream()
                .filter(d -> asignaturasCriticasIds.contains(d.getAsignaturaId()))
                .map(Disponibilidad::getId)
                .collect(Collectors.toSet());

        // 3. Obtener el set de estudianteIds que tienen al menos una cita en esas asignaturas críticas
        Set<String> estudiantesConCitaEnCriticas = citaRepository.findAll().stream()
                .filter(c -> disponibilidadesCriticasIds.contains(c.getDisponibilidadId()))
                .map(Cita::getEstudianteId)
                .collect(Collectors.toSet());

        // 4. Filtrar estudiantes activos que NO están en dicho set
        return usuarioRepository.findByRol(Rol.ESTUDIANTE).stream()
                .filter(Usuario::isActivo)
                .filter(e -> {
                    if (rol == Rol.DIRECTOR && programaUsuario != null) {
                        return programaUsuario.equalsIgnoreCase(e.getPrograma());
                    }
                    return true;
                })
                .filter(e -> !estudiantesConCitaEnCriticas.contains(e.getId()))
                .sorted(Comparator.comparing(Usuario::getNombre))
                .map(e -> new EstudianteRiesgoDto(e.getNombre(), e.getCorreo(), e.getPrograma()))
                .toList();
    }
}
