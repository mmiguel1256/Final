package co.edu.uts.tutorias.config;

import co.edu.uts.tutorias.model.Rol;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

/**
 * Interceptor de seguridad para autorización por roles y protección de rutas.
 */
@Component
public class AccesoInterceptor implements HandlerInterceptor {

    private final SesionUsuario sesionUsuario;
    private final LinkedHashMap<String, Set<Rol>> permisosRuta = new LinkedHashMap<>();

    public AccesoInterceptor(SesionUsuario sesionUsuario) {
        this.sesionUsuario = sesionUsuario;
        configurarPermisos();
    }

    private void configurarPermisos() {
        // Rutas más específicas primero
        permisosRuta.put("/reportes/asignaturas", Set.of(Rol.COORDINADOR, Rol.DIRECTOR, Rol.ADMIN));
        permisosRuta.put("/reportes/participacion", Set.of(Rol.BIENESTAR, Rol.ADMIN));
        permisosRuta.put("/reportes", Set.of(Rol.COORDINADOR, Rol.DIRECTOR, Rol.ADMIN, Rol.BIENESTAR));

        permisosRuta.put("/admin", Set.of(Rol.ADMIN));
        permisosRuta.put("/docente", Set.of(Rol.DOCENTE, Rol.MONITOR));
        permisosRuta.put("/estudiante", Set.of(Rol.ESTUDIANTE));
        permisosRuta.put("/citas", Set.of(Rol.ESTUDIANTE, Rol.DOCENTE, Rol.MONITOR));
        permisosRuta.put("/foro", Set.of(Rol.ESTUDIANTE, Rol.DOCENTE, Rol.MONITOR));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String ruta = (contextPath != null && !contextPath.isEmpty()) ? uri.substring(contextPath.length()) : uri;

        // Rutas públicas
        if (esRutaPublica(ruta)) {
            return true;
        }

        // Si no está autenticado, redirigir a /login
        if (!sesionUsuario.estaAutenticado()) {
            response.sendRedirect(contextPath + "/login");
            return false;
        }

        // Validar rol según prefijo de ruta
        Rol rolUsuario = sesionUsuario.getRol();
        for (Map.Entry<String, Set<Rol>> entrada : permisosRuta.entrySet()) {
            String prefijo = entrada.getKey();
            if (ruta.equals(prefijo) || ruta.startsWith(prefijo + "/") || ruta.startsWith(prefijo + "?")) {
                if (!entrada.getValue().contains(rolUsuario)) {
                    response.sendRedirect(contextPath + "/acceso-denegado");
                    return false;
                }
                return true;
            }
        }

        return true;
    }

    private boolean esRutaPublica(String ruta) {
        return ruta.equals("/") ||
                ruta.equals("/login") ||
                ruta.equals("/logout") ||
                ruta.equals("/acceso-denegado") ||
                ruta.startsWith("/css/") ||
                ruta.startsWith("/js/") ||
                ruta.startsWith("/images/") ||
                ruta.startsWith("/error");
    }
}
