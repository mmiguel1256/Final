package co.edu.uts.tutorias.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "asignaturas")
public class Asignatura {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nombre;

    private String programa;

    private boolean critica;

    public Asignatura() {
    }

    public Asignatura(String id, String codigo, String nombre, String programa, boolean critica) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.programa = programa;
        this.critica = critica;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
