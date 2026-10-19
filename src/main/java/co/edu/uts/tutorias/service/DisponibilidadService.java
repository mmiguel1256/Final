package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.DisponibilidadDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Disponibilidad;
import co.edu.uts.tutorias.model.EstadoDisponibilidad;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.repository.DisponibilidadRepository;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión y validación de franjas de disponibilidad de docentes y monitores.
 */
@Service
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;

    public DisponibilidadService(DisponibilidadRepository disponibilidadRepository,
                                 UsuarioRepository usuarioRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Disponibilidad> listarPorDocente(String docenteId) {
        List<Disponibilidad> lista = disponibilidadRepository.findByDocenteId(docenteId);
        lista.sort(Comparator.comparing(Disponibilidad::getFecha).thenComparing(Disponibilidad::getHoraInicio));
        return lista;
    }

    public List<Disponibilidad> listarLibresPorAsignatura(String asignaturaId) {
        return disponibilidadRepository.findByAsignaturaIdAndEstado(asignaturaId, EstadoDisponibilidad.LIBRE);
    }

    public List<Disponibilidad> listarTodasLibres() {
        return disponibilidadRepository.findByEstado(EstadoDisponibilidad.LIBRE);
    }

    public Optional<Disponibilidad> buscarPorId(String id) {
        return disponibilidadRepository.findById(id);
    }

    /**
     * Publica una nueva franja de disponibilidad validando las reglas de negocio:
     * 1. La fecha debe ser futura.
     * 2. horaFin debe ser estrictamente posterior a horaInicio.
     * 3. El docente o monitor solo puede publicar en asignaturas que tenga asignadas.
     */
    public Disponibilidad crearFranja(String docenteId, DisponibilidadDto dto) {
        Usuario docente = usuarioRepository.findById(docenteId)
                .orElseThrow(() -> new ReglaNegocioException("Docente/Monitor no encontrado en el sistema."));

        // Regla: Solo publican en las asignaturas que tienen asignadas (Sección 6)
        if (docente.getAsignaturasIds() == null || !docente.getAsignaturasIds().contains(dto.getAsignaturaId())) {
            throw new ReglaNegocioException("No tienes asignada esta asignatura para publicar tutorías.");
        }

        // Regla: Fecha futura (Sección 6)
        if (dto.getFecha() == null || !dto.getFecha().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de la disponibilidad debe ser una fecha futura.");
        }

        // Regla: horaFin posterior a horaInicio (Sección 6)
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null || !dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new ReglaNegocioException("La hora de finalización debe ser posterior a la hora de inicio.");
        }

        // Validar no solapamiento con franjas existentes del mismo docente en la misma fecha
        List<Disponibilidad> existentes = disponibilidadRepository.findByDocenteIdAndFecha(docenteId, dto.getFecha());
        for (Disponibilidad disp : existentes) {
            boolean solapa = !(dto.getHoraFin().isBefore(disp.getHoraInicio()) || dto.getHoraFin().equals(disp.getHoraInicio())
                    || dto.getHoraInicio().isAfter(disp.getHoraFin()) || dto.getHoraInicio().equals(disp.getHoraFin()));
            if (solapa) {
                throw new ReglaNegocioException("Ya tienes una franja horaria programada que se cruza con este horario ("
                        + disp.getHoraInicio() + " - " + disp.getHoraFin() + ").");
            }
        }

        Disponibilidad nueva = new Disponibilidad(
                null,
                docenteId,
                dto.getAsignaturaId(),
                dto.getFecha(),
                dto.getHoraInicio(),
                dto.getHoraFin(),
                dto.getModalidad(),
                dto.getLugar(),
                EstadoDisponibilidad.LIBRE
        );

        return disponibilidadRepository.save(nueva);
    }

    /**
     * Elimina una franja de disponibilidad solo si pertenece al docente y está en estado LIBRE.
     */
    public void eliminarFranja(String docenteId, String disponibilidadId) {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new ReglaNegocioException("La disponibilidad indicada no existe."));

        if (!disponibilidad.getDocenteId().equals(docenteId)) {
            throw new ReglaNegocioException("No tienes autorización para eliminar esta disponibilidad.");
        }

        if (disponibilidad.getEstado() != EstadoDisponibilidad.LIBRE) {
            throw new ReglaNegocioException("No se puede eliminar una franja que ya ha sido reservada por un estudiante.");
        }

        disponibilidadRepository.delete(disponibilidad);
    }

    public Disponibilidad guardar(Disponibilidad disponibilidad) {
        return disponibilidadRepository.save(disponibilidad);
    }
}
