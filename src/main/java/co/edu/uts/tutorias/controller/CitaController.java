package co.edu.uts.tutorias.controller;

import co.edu.uts.tutorias.config.SesionUsuario;
import co.edu.uts.tutorias.dto.RespuestaSolicitudDto;
import co.edu.uts.tutorias.dto.SolicitudCitaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para las acciones transaccionales sobre citas:
 * solicitar, cancelar, aceptar y rechazar.
 */
@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final SesionUsuario sesionUsuario;

    public CitaController(CitaService citaService, SesionUsuario sesionUsuario) {
        this.citaService = citaService;
        this.sesionUsuario = sesionUsuario;
    }

    /**
     * Solicitud de tutoría por parte de un estudiante.
     * POST /citas
     */
    @PostMapping
    public String solicitarCita(@Valid @ModelAttribute("solicitudCitaDto") SolicitudCitaDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensajeError", "El motivo o tema de la tutoría es obligatorio (mínimo 5 caracteres).");
            return "redirect:/estudiante/tutorias";
        }

        try {
            citaService.solicitarCita(sesionUsuario.getId(), dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Tutoría solicitada exitosamente! Tu solicitud quedó en estado PENDIENTE.");
            return "redirect:/estudiante/citas";
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/estudiante/tutorias";
        }
    }

    /**
     * Cancelación de cita por parte de un estudiante.
     * POST /citas/{id}/cancelar
     */
    @PostMapping("/{id}/cancelar")
    public String cancelarCita(@PathVariable("id") String id,
                               RedirectAttributes redirectAttributes) {
        try {
            citaService.cancelarCita(sesionUsuario.getId(), id);
            redirectAttributes.addFlashAttribute("mensajeExito", "La cita ha sido cancelada y la franja horaria fue liberada.");
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/estudiante/citas";
    }

    /**
     * Aceptación de solicitud por parte del docente o monitor.
     * POST /citas/{id}/aceptar
     */
    @PostMapping("/{id}/aceptar")
    public String aceptarCita(@PathVariable("id") String id,
                              @ModelAttribute RespuestaSolicitudDto dto,
                              RedirectAttributes redirectAttributes) {
        try {
            String comentario = (dto != null) ? dto.getComentarioDocente() : null;
            citaService.aceptarCita(sesionUsuario.getId(), id, comentario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud de tutoría ACEPTADA correctamente.");
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/docente/solicitudes";
    }

    /**
     * Rechazo de solicitud por parte del docente o monitor.
     * POST /citas/{id}/rechazar
     */
    @PostMapping("/{id}/rechazar")
    public String rechazarCita(@PathVariable("id") String id,
                              @ModelAttribute RespuestaSolicitudDto dto,
                              RedirectAttributes redirectAttributes) {
        try {
            String comentario = (dto != null) ? dto.getComentarioDocente() : null;
            citaService.rechazarCita(sesionUsuario.getId(), id, comentario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud de tutoría RECHAZADA. La franja volvió a quedar en estado LIBRE.");
        } catch (ReglaNegocioException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/docente/solicitudes";
    }
}
