package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.SolicitudCitaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.*;
import co.edu.uts.tutorias.repository.CitaRepository;
import co.edu.uts.tutorias.repository.DisponibilidadRepository;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de negocio para la gestión, reserva, aceptación y cancelación de citas de tutoría.
 */
@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;

    public CitaService(CitaRepository citaRepository,
                       DisponibilidadRepository disponibilidadRepository,
                       UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Solicita una cita de tutoría por parte de un estudiante.
     * Reglas de negocio:
     * 1. La franja debe estar en estado LIBRE (si está RESERVADA, falla).
     * 2. El estudiante no puede tener dos citas activas (PENDIENTE o ACEPTADA) en la misma fecha y horario.
     * 3. Al reservar, la franja pasa a RESERVADA y la cita se crea en PENDIENTE.
     */
    public Cita solicitarCita(String estudianteId, SolicitudCitaDto dto) {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(dto.getDisponibilidadId())
                .orElseThrow(() -> new ReglaNegocioException("La franja de tutoría seleccionada no existe."));

        // Regla: Una franja solo puede tener una cita activa: al solicitarla pasa a RESERVADA
        if (disponibilidad.getEstado() != EstadoDisponibilidad.LIBRE) {
            throw new ReglaNegocioException("La franja seleccionada ya ha sido reservada por otro estudiante.");
        }

        // Regla: Un estudiante no puede tener dos citas activas (PENDIENTE o ACEPTADA) en la misma fecha y hora
        List<Cita> citasActivas = citaRepository.findByEstudianteIdAndEstadoIn(
                estudianteId,
                Arrays.asList(EstadoCita.PENDIENTE, EstadoCita.ACEPTADA)
        );

        for (Cita citaActiva : citasActivas) {
            Optional<Disponibilidad> dispExistenteOpt = disponibilidadRepository.findById(citaActiva.getDisponibilidadId());
            if (dispExistenteOpt.isPresent()) {
                Disponibilidad dispExistente = dispExistenteOpt.get();
                if (dispExistente.getFecha().equals(disponibilidad.getFecha())) {
                    // Validar si los rangos de horas se solapan
                    boolean seCruzan = !(disponibilidad.getHoraFin().isBefore(dispExistente.getHoraInicio()) ||
                            disponibilidad.getHoraFin().equals(dispExistente.getHoraInicio()) ||
                            disponibilidad.getHoraInicio().isAfter(dispExistente.getHoraFin()) ||
                            disponibilidad.getHoraInicio().equals(dispExistente.getHoraFin()));
                    if (seCruzan) {
                        throw new ReglaNegocioException("Ya tienes una cita activa (" + citaActiva.getEstado()
                                + ") programada para la misma fecha y horario ("
                                + dispExistente.getHoraInicio() + " - " + dispExistente.getHoraFin() + ").");
                    }
                }
            }
        }

        // Cambiar estado de la franja a RESERVADA
        disponibilidad.setEstado(EstadoDisponibilidad.RESERVADA);
        disponibilidadRepository.save(disponibilidad);

        // Crear la cita en estado PENDIENTE
        Cita nuevaCita = new Cita(
                null,
                estudianteId,
                disponibilidad.getId(),
                dto.getTema().trim(),
                EstadoCita.PENDIENTE,
                null,
                LocalDateTime.now()
        );

        return citaRepository.save(nuevaCita);
    }

    /**
     * Cancela una cita de tutoría por parte del estudiante.
     * Reglas de negocio:
     * 1. Solo se puede cancelar si está PENDIENTE o ACEPTADA.
     * 2. Al cancelar, la cita pasa a CANCELADA y la franja vuelve a LIBRE.
     */
    public Cita cancelarCita(String estudianteId, String citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ReglaNegocioException("La cita que intentas cancelar no existe."));

        if (!cita.getEstudianteId().equals(estudianteId)) {
            throw new ReglaNegocioException("No tienes autorización para cancelar esta cita.");
        }

        // Regla: Cancelar solo si la cita está PENDIENTE o ACEPTADA (Sección 6)
        if (cita.getEstado() != EstadoCita.PENDIENTE && cita.getEstado() != EstadoCita.ACEPTADA) {
            throw new ReglaNegocioException("Solo se pueden cancelar citas en estado PENDIENTE o ACEPTADA.");
        }

        cita.setEstado(EstadoCita.CANCELADA);

        // Regla: si el estudiante cancela, la franja vuelve a LIBRE
        disponibilidadRepository.findById(cita.getDisponibilidadId()).ifPresent(disp -> {
            disp.setEstado(EstadoDisponibilidad.LIBRE);
            disponibilidadRepository.save(disp);
        });

        return citaRepository.save(cita);
    }

    /**
     * Acepta una cita por parte del docente o monitor.
     */
    public Cita aceptarCita(String docenteId, String citaId, String comentario) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ReglaNegocioException("La cita especificada no existe."));

        Disponibilidad disponibilidad = disponibilidadRepository.findById(cita.getDisponibilidadId())
                .orElseThrow(() -> new ReglaNegocioException("La disponibilidad vinculada a esta cita no existe."));

        if (!disponibilidad.getDocenteId().equals(docenteId)) {
            throw new ReglaNegocioException("No tienes autorización para gestionar solicitudes de otro docente.");
        }

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden aceptar citas que se encuentren en estado PENDIENTE.");
        }

        cita.setEstado(EstadoCita.ACEPTADA);
        if (comentario != null && !comentario.trim().isEmpty()) {
            cita.setComentarioDocente(comentario.trim());
        }

        return citaRepository.save(cita);
    }

    /**
     * Rechaza una cita por parte del docente o monitor.
     * Regla: Al rechazar, la cita queda en RECHAZADA y la franja vuelve a LIBRE.
     */
    public Cita rechazarCita(String docenteId, String citaId, String comentario) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ReglaNegocioException("La cita especificada no existe."));

        Disponibilidad disponibilidad = disponibilidadRepository.findById(cita.getDisponibilidadId())
                .orElseThrow(() -> new ReglaNegocioException("La disponibilidad vinculada a esta cita no existe."));

        if (!disponibilidad.getDocenteId().equals(docenteId)) {
            throw new ReglaNegocioException("No tienes autorización para gestionar solicitudes de otro docente.");
        }

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden rechazar citas en estado PENDIENTE.");
        }

        cita.setEstado(EstadoCita.RECHAZADA);
        if (comentario != null && !comentario.trim().isEmpty()) {
            cita.setComentarioDocente(comentario.trim());
        }

        // Regla: si el docente rechaza, vuelve a LIBRE
        disponibilidad.setEstado(EstadoDisponibilidad.LIBRE);
        disponibilidadRepository.save(disponibilidad);

        return citaRepository.save(cita);
    }

    public List<Cita> listarPorEstudiante(String estudianteId) {
        List<Cita> citas = citaRepository.findByEstudianteId(estudianteId);
        citas.sort(Comparator.comparing(Cita::getFechaSolicitud).reversed());
        return citas;
    }

    public List<Cita> listarSolicitudesPorDocente(String docenteId) {
        List<Disponibilidad> franjasDocente = disponibilidadRepository.findByDocenteId(docenteId);
        if (franjasDocente.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> dispIds = new ArrayList<>();
        for (Disponibilidad d : franjasDocente) {
            dispIds.add(d.getId());
        }

        List<Cita> citas = citaRepository.findByDisponibilidadIdIn(dispIds);
        citas.sort(Comparator.comparing(Cita::getFechaSolicitud).reversed());
        return citas;
    }

    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> buscarPorId(String id) {
        return citaRepository.findById(id);
    }
}
