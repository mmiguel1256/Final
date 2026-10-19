package co.edu.uts.tutorias.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "publicaciones")
public class Publicacion {

    @Id
    private String id;

    private String asignaturaId;

    private String autorId;

    private String titulo;

    private String contenido;

    private LocalDateTime fecha;

    private List<Respuesta> respuestas = new ArrayList<>();

    public Publicacion() {
    }

    public Publicacion(String id, String asignaturaId, String autorId, String titulo, String contenido, LocalDateTime fecha, List<Respuesta> respuestas) {
        this.id = id;
        this.asignaturaId = asignaturaId;
        this.autorId = autorId;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fecha = fecha;
        this.respuestas = (respuestas != null) ? respuestas : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(String asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
