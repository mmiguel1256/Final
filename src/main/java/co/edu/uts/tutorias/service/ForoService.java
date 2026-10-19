package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.PublicacionDto;
import co.edu.uts.tutorias.dto.RespuestaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Publicacion;
import co.edu.uts.tutorias.model.Respuesta;
import co.edu.uts.tutorias.repository.AsignaturaRepository;
import co.edu.uts.tutorias.repository.PublicacionRepository;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para foros colaborativos por asignatura.
 */
@Service
public class ForoService {

    private final PublicacionRepository publicacionRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final UsuarioRepository usuarioRepository;

    public ForoService(PublicacionRepository publicacionRepository,
                       AsignaturaRepository asignaturaRepository,
                       UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Publicacion> listarPorAsignatura(String asignaturaId) {
        return publicacionRepository.findByAsignaturaIdOrderByFechaDesc(asignaturaId);
    }

    public Optional<Publicacion> buscarPorId(String id) {
        return publicacionRepository.findById(id);
    }

    public long contarPorAsignatura(String asignaturaId) {
        return publicacionRepository.findByAsignaturaIdOrderByFechaDesc(asignaturaId).size();
    }

    /**
     * Crea una nueva publicación en el foro de una asignatura.
     */
    public Publicacion crearPublicacion(String asignaturaId, String autorId, PublicacionDto dto) {
        if (!asignaturaRepository.existsById(asignaturaId)) {
            throw new ReglaNegocioException("La asignatura para este foro no existe.");
        }

        if (!usuarioRepository.existsById(autorId)) {
            throw new ReglaNegocioException("El autor de la publicación no existe.");
        }

        String titulo = (dto.getTitulo() != null) ? dto.getTitulo().trim() : "";
        String contenido = (dto.getContenido() != null) ? dto.getContenido().trim() : "";

        // Reglas de negocio de longitud (Sección 6)
        if (titulo.length() < 3 || titulo.length() > 120) {
            throw new ReglaNegocioException("El título del foro debe tener entre 3 y 120 caracteres.");
        }

        if (contenido.length() < 5 || contenido.length() > 1000) {
            throw new ReglaNegocioException("El contenido del mensaje debe tener entre 5 y 1000 caracteres.");
        }

        Publicacion publicacion = new Publicacion(
                null,
                asignaturaId,
                autorId,
                titulo,
                contenido,
                LocalDateTime.now(),
                new ArrayList<>()
        );

        return publicacionRepository.save(publicacion);
    }

    /**
     * Agrega una respuesta a una publicación existente en el foro.
     */
    public Publicacion agregarRespuesta(String publicacionId, String autorId, RespuestaDto dto) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ReglaNegocioException("La publicación a la que intentas responder no existe."));

        if (!usuarioRepository.existsById(autorId)) {
            throw new ReglaNegocioException("El autor de la respuesta no existe.");
        }

        String texto = (dto.getTexto() != null) ? dto.getTexto().trim() : "";

        // Regla de longitud para respuestas (Sección 6)
        if (texto.length() < 2 || texto.length() > 500) {
            throw new ReglaNegocioException("La respuesta debe tener entre 2 y 500 caracteres.");
        }

        Respuesta respuesta = new Respuesta(
                autorId,
                texto,
                LocalDateTime.now()
        );

        if (publicacion.getRespuestas() == null) {
            publicacion.setRespuestas(new ArrayList<>());
        }
        publicacion.getRespuestas().add(respuesta);

        return publicacionRepository.save(publicacion);
    }
}
