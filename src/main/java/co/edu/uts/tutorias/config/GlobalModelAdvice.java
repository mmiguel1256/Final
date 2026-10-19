package co.edu.uts.tutorias.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone automáticamente el objeto SesionUsuario a todas las vistas Thymeleaf
 * bajo el nombre de modelo "sesion".
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final SesionUsuario sesionUsuario;

    public GlobalModelAdvice(SesionUsuario sesionUsuario) {
        this.sesionUsuario = sesionUsuario;
    }

    @ModelAttribute("sesion")
    public SesionUsuario obtenerSesion() {
        return this.sesionUsuario;
    }
}
