package co.edu.uts.tutorias.dto;

/**
 * Formulario DTO para comentarios al aceptar o rechazar una solicitud de tutoría.
 */
public class RespuestaSolicitudDto {

    private String comentarioDocente;

    public RespuestaSolicitudDto() {
    }

    public RespuestaSolicitudDto(String comentarioDocente) {
        this.comentarioDocente = comentarioDocente;
    }

    public String getComentarioDocente() {
        return comentarioDocente;
    }

    public void setComentarioDocente(String comentarioDocente) {
        this.comentarioDocente = (comentarioDocente != null) ? comentarioDocente.trim() : null;
    }
}
