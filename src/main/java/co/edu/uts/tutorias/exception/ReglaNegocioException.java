package co.edu.uts.tutorias.exception;

/**
 * Excepción no verificada para infracciones de reglas de negocio del sistema.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
