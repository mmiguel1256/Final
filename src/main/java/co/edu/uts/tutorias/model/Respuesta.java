package co.edu.uts.tutorias.model;

import java.time.LocalDateTime;

public class Respuesta {

    private String autorId;

    private String texto;

    private LocalDateTime fecha;

    public Respuesta() {
    }

    public Respuesta(String autorId, String texto, LocalDateTime fecha) {
        this.autorId = autorId;
        this.texto = texto;
        this.fecha = fecha;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
