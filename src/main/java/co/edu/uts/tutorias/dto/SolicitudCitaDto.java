package co.edu.uts.tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Formulario DTO para que un estudiante solicite una tutoría.
 */
public class SolicitudCitaDto {

    @NotBlank(message = "La franja de disponibilidad es obligatoria")
    private String disponibilidadId;

    @NotBlank(message = "El tema o motivo de la tutoría es obligatorio")
    @Size(min = 5, max = 200, message = "El tema debe tener entre 5 y 200 caracteres")
    private String tema;

    public SolicitudCitaDto() {
    }

    public SolicitudCitaDto(String disponibilidadId, String tema) {
        this.disponibilidadId = disponibilidadId;
        this.tema = tema;
    }

    public String getDisponibilidadId() {
        return disponibilidadId;
    }

    public void setDisponibilidadId(String disponibilidadId) {
        this.disponibilidadId = disponibilidadId;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = (tema != null) ? tema.trim() : null;
    }
}
