package co.edu.uts.tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Formulario DTO para responder a una publicación en el foro.
 * Regla: Respuesta de 2 a 500 caracteres (Sección 6).
 */
public class RespuestaDto {

    @NotBlank(message = "El texto de la respuesta es obligatorio")
    @Size(min = 2, max = 500, message = "La respuesta debe tener entre 2 y 500 caracteres")
    private String texto;

    public RespuestaDto() {
    }

    public RespuestaDto(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = (texto != null) ? texto.trim() : null;
    }
}
