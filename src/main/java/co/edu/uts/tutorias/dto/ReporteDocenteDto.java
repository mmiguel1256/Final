package co.edu.uts.tutorias.dto;

/**
 * DTO para consolidar métricas de tutorías atendidas por docente y monitor.
 */
public class ReporteDocenteDto {

    private String docenteNombre;
    private String docenteCorreo;
    private String rol;
    private String programa;
    private long totalFranjas;
    private long franjasLibres;
    private long franjasReservadas;
    private long citasAceptadas;
    private long citasRechazadas;

    public ReporteDocenteDto() {
    }

    public ReporteDocenteDto(String docenteNombre, String docenteCorreo, String rol, String programa,
                             long totalFranjas, long franjasLibres, long franjasReservadas,
                             long citasAceptadas, long citasRechazadas) {
        this.docenteNombre = docenteNombre;
        this.docenteCorreo = docenteCorreo;
        this.rol = rol;
        this.programa = programa;
        this.totalFranjas = totalFranjas;
        this.franjasLibres = franjasLibres;
        this.franjasReservadas = franjasReservadas;
        this.citasAceptadas = citasAceptadas;
        this.citasRechazadas = citasRechazadas;
    }

    public String getDocenteNombre() {
        return docenteNombre;
    }

    public void setDocenteNombre(String docenteNombre) {
        this.docenteNombre = docenteNombre;
    }

    public String getDocenteCorreo() {
        return docenteCorreo;
    }

    public void setDocenteCorreo(String docenteCorreo) {
        this.docenteCorreo = docenteCorreo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public long getTotalFranjas() {
        return totalFranjas;
    }

    public void setTotalFranjas(long totalFranjas) {
        this.totalFranjas = totalFranjas;
    }

    public long getFranjasLibres() {
        return franjasLibres;
    }

    public void setFranjasLibres(long franjasLibres) {
        this.franjasLibres = franjasLibres;
    }

    public long getFranjasReservadas() {
        return franjasReservadas;
    }

    public void setFranjasReservadas(long franjasReservadas) {
        this.franjasReservadas = franjasReservadas;
    }

    public long getCitasAceptadas() {
        return citasAceptadas;
    }

    public void setCitasAceptadas(long citasAceptadas) {
        this.citasAceptadas = citasAceptadas;
    }

    public long getCitasRechazadas() {
        return citasRechazadas;
    }

    public void setCitasRechazadas(long citasRechazadas) {
        this.citasRechazadas = citasRechazadas;
    }
}
