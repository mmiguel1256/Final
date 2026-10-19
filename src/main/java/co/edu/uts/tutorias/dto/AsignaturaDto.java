package co.edu.uts.tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Formulario DTO para registro de asignaturas.
 */
public class AsignaturaDto {

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 20, message = "El código debe tener entre 2 y 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El programa académico es obligatorio")
    private String programa;

    private boolean critica;

    public AsignaturaDto() {
    }

    public AsignaturaDto(String codigo, String nombre, String programa, boolean critica) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.programa = programa;
        this.critica = critica;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = (codigo != null) ? codigo.trim().toUpperCase() : null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.trim() : null;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = (programa != null) ? programa.trim() : null;
    }

    public boolean isCritica() {
        return critica;
    }

    public void setCritica(boolean critica) {
        this.critica = critica;
    }
}
