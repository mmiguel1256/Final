package co.edu.uts.tutorias.dto;

/**
 * DTO para consolidar métricas de citas por asignatura y estado.
 */
public class ReporteAsignaturaEstadoDto {

    private String codigo;
    private String nombre;
    private String programa;
    private boolean critica;
    private long totalCitas;
    private long pendientes;
    private long aceptadas;
    private long rechazadas;
    private long canceladas;

    public ReporteAsignaturaEstadoDto() {
    }

    public ReporteAsignaturaEstadoDto(String codigo, String nombre, String programa, boolean critica,
                                     long totalCitas, long pendientes, long aceptadas, long rechazadas, long canceladas) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.programa = programa;
        this.critica = critica;
        this.totalCitas = totalCitas;
        this.pendientes = pendientes;
        this.aceptadas = aceptadas;
        this.rechazadas = rechazadas;
        this.canceladas = canceladas;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public boolean isCritica() {
        return critica;
    }

    public void setCritica(boolean critica) {
        this.critica = critica;
    }

    public long getTotalCitas() {
        return totalCitas;
    }

    public void setTotalCitas(long totalCitas) {
        this.totalCitas = totalCitas;
    }

    public long getPendientes() {
        return pendientes;
    }

    public void setPendientes(long pendientes) {
        this.pendientes = pendientes;
    }

    public long getAceptadas() {
        return aceptadas;
    }

    public void setAceptadas(long aceptadas) {
        this.aceptadas = aceptadas;
    }

    public long getRechazadas() {
        return rechazadas;
    }

    public void setRechazadas(long rechazadas) {
        this.rechazadas = rechazadas;
    }

    public long getCanceladas() {
        return canceladas;
    }

    public void setCanceladas(long canceladas) {
        this.canceladas = canceladas;
    }
}
