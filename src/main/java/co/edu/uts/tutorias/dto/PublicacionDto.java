package co.edu.uts.tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Formulario DTO para crear una nueva publicación en el foro.
 * Regla: Título de 3 a 120 caracteres y contenido de 5 a 1000 caracteres (Sección 6).
 */
public class PublicacionDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(min = 5, max = 1000, message = "El contenido debe tener entre 5 y 1000 caracteres")
    private String contenido;

    public PublicacionDto() {
    }

    public PublicacionDto(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = (titulo != null) ? titulo.trim() : null;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = (contenido != null) ? contenido.trim() : null;
    }
}
