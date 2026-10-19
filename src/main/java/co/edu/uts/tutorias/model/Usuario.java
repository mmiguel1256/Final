package co.edu.uts.tutorias.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Indexed(unique = true)
    private String correo;

    private Rol rol;

    private String programa;

    private List<String> asignaturasIds = new ArrayList<>();

    private boolean activo;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String correo, Rol rol, String programa, List<String> asignaturasIds, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.programa = programa;
        this.asignaturasIds = (asignaturasIds != null) ? asignaturasIds : new ArrayList<>();
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.programa = programa;
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
