package co.edu.uts.tutorias.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "citas")
public class Cita {

    @Id
    private String id;

    private String estudianteId;

    private String disponibilidadId;

    private String tema;

    private EstadoCita estado;

    private String comentarioDocente;

    private LocalDateTime fechaSolicitud;

    public Cita() {
    }

    public Cita(String id, String estudianteId, String disponibilidadId, String tema, EstadoCita estado, String comentarioDocente, LocalDateTime fechaSolicitud) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.disponibilidadId = disponibilidadId;
        this.tema = tema;
        this.estado = estado;
        this.comentarioDocente = comentarioDocente;
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(String estudianteId) {
        this.estudianteId = estudianteId;
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
        this.tema = tema;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getComentarioDocente() {
        return comentarioDocente;
    }

    public void setComentarioDocente(String comentarioDocente) {
        this.comentarioDocente = comentarioDocente;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
