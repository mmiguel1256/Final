package co.edu.uts.tutorias.config;

import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

/**
 * Componente de ámbito de sesión para almacenar los datos del usuario autenticado.
 * Es la única forma de guardar la sesión en la aplicación.
 */
@Component
@SessionScope
public class SesionUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String correo;
    private Rol rol;
    private String programa;

    public void iniciar(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.rol = usuario.getRol();
        this.programa = usuario.getPrograma();
    }

    public void cerrar() {
        this.id = null;
        this.nombre = null;
        this.correo = null;
        this.rol = null;
        this.programa = null;
    }

    public boolean estaAutenticado() {
        return this.correo != null && this.rol != null;
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
}
