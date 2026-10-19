package co.edu.uts.tutorias.dto;

import co.edu.uts.tutorias.model.Rol;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Formulario DTO para actualizar el rol, asignaturas asignadas y estado activo de un usuario.
 */
public class UsuarioRolDto {

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private List<String> asignaturasIds = new ArrayList<>();

    private boolean activo;

    public UsuarioRolDto() {
    }

    public UsuarioRolDto(Rol rol, List<String> asignaturasIds, boolean activo) {
        this.rol = rol;
        this.asignaturasIds = (asignaturasIds != null) ? asignaturasIds : new ArrayList<>();
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<String> getAsignaturasIds() {
        return asignaturasIds;
    }

    public void setAsignaturasIds(List<String> asignaturasIds) {
        this.asignaturasIds = asignaturasIds;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
