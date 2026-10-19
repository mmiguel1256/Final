package co.edu.uts.tutorias.dto;

/**
 * DTO para el reporte de Bienestar Institucional:
 * Estudiantes activos sin ninguna cita en asignaturas críticas (sin exponer tema ni comentarios).
 */
public class EstudianteRiesgoDto {

    private String nombre;
    private String correo;
    private String programa;

    public EstudianteRiesgoDto() {
    }

    public EstudianteRiesgoDto(String nombre, String correo, String programa) {
        this.nombre = nombre;
        this.correo = correo;
        this.programa = programa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }
}
