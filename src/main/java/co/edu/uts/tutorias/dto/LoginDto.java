package co.edu.uts.tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Formulario DTO para inicio de sesión mediante correo institucional.
 */
public class LoginDto {

    @NotBlank(message = "El correo es obligatorio")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@correo\\.uts\\.edu\\.co$",
            message = "Solo se permite correo institucional con dominio @correo.uts.edu.co"
    )
    private String correo;

    public LoginDto() {
    }

    public LoginDto(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
