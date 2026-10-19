package co.edu.uts.tutorias.dto;

import co.edu.uts.tutorias.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Formulario DTO para creación de nuevos usuarios por parte del Administrador.
 */
public class UsuarioCrearDto {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@correo\\.uts\\.edu\\.co$",
            message = "Solo se permite correo institucional con dominio @correo.uts.edu.co"
    )
    private String correo;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @NotBlank(message = "El programa académico es obligatorio")
    private String programa;

    private List<String> asignaturasIds = new ArrayList<>();

    public UsuarioCrearDto() {
    }

    public UsuarioCrearDto(String nombre, String correo, Rol rol, String programa, List<String> asignaturasIds) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.programa = programa;
        this.asignaturasIds = (asignaturasIds != null) ? asignaturasIds : new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.trim() : null;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = (correo != null) ? correo.trim().toLowerCase() : null;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = (programa != null) ? programa.trim() : null;
    }

    public List<String> getAsignaturasIds() {
        return asignaturasIds;
    }

    public void setAsignaturasIds(List<String> asignaturasIds) {
        this.asignaturasIds = asignaturasIds;
    }
}
