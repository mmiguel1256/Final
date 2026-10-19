package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.UsuarioCrearDto;
import co.edu.uts.tutorias.dto.UsuarioRolDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión y autenticación de usuarios.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica a un usuario por su correo institucional.
     * Valida existencia y estado activo.
     */
    public Usuario autenticarPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe ingresar un correo electrónico.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase())
                .orElseThrow(() -> new ReglaNegocioException("No se encontró ningún usuario registrado con el correo: " + correo));

        if (!usuario.isActivo()) {
            throw new ReglaNegocioException("La cuenta de usuario asociada a este correo se encuentra inactiva. Contacte al administrador.");
        }

        return usuario;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Crea un nuevo usuario verificando que el correo no esté registrado previamente.
     */
    public Usuario crear(UsuarioCrearDto dto) {
        String correoLimpio = dto.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            throw new ReglaNegocioException("Ya existe un usuario registrado con el correo: " + correoLimpio);
        }

        List<String> asignaturas = new ArrayList<>();
        if (dto.getRol() == Rol.DOCENTE || dto.getRol() == Rol.MONITOR) {
            if (dto.getAsignaturasIds() != null) {
                asignaturas = dto.getAsignaturasIds();
            }
        }

        Usuario usuario = new Usuario(
                null,
                dto.getNombre().trim(),
                correoLimpio,
                dto.getRol(),
                dto.getPrograma().trim(),
                asignaturas,
                true // activo por defecto al crearse
        );

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza el rol, asignaturas asignadas y estado de activación de un usuario existente.
     */
    public Usuario actualizarRolYAsignaturas(String id, UsuarioRolDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el usuario con ID: " + id));

        usuario.setRol(dto.getRol());
        usuario.setActivo(dto.isActivo());

        // Asignaturas solo aplican a DOCENTE y MONITOR (Sección 5.1)
        if (dto.getRol() == Rol.DOCENTE || dto.getRol() == Rol.MONITOR) {
            usuario.setAsignaturasIds(dto.getAsignaturasIds() != null ? dto.getAsignaturasIds() : new ArrayList<>());
        } else {
            usuario.setAsignaturasIds(new ArrayList<>());
        }

        return usuarioRepository.save(usuario);
    }
}
