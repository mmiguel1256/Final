package co.edu.uts.tutorias.dto;

import co.edu.uts.tutorias.model.Modalidad;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Formulario DTO para publicación de franjas horarias de disponibilidad.
 */
public class DisponibilidadDto {

    @NotBlank(message = "Debe seleccionar una asignatura asignada")
    private String asignaturaId;

    @NotNull(message = "La fecha es obligatoria")
    @Future(message = "La fecha de la tutoría debe ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de finalización es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaFin;

    @NotNull(message = "La modalidad es obligatoria")
    private Modalidad modalidad;

    @NotBlank(message = "El lugar o enlace virtual es obligatorio")
    private String lugar;

    public DisponibilidadDto() {
    }

    public DisponibilidadDto(String asignaturaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Modalidad modalidad, String lugar) {
        this.asignaturaId = asignaturaId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.modalidad = modalidad;
        this.lugar = lugar;
    }

    public String getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(String asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = (lugar != null) ? lugar.trim() : null;
    }
}
