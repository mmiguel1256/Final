package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.AsignaturaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Asignatura;
import co.edu.uts.tutorias.repository.AsignaturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la administración del catálogo de asignaturas.
 */
@Service
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;

    public AsignaturaService(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    public List<Asignatura> listarTodas() {
        return asignaturaRepository.findAll();
    }

    public Optional<Asignatura> buscarPorId(String id) {
        return asignaturaRepository.findById(id);
    }

    public List<Asignatura> buscarPorPrograma(String programa) {
        return asignaturaRepository.findByPrograma(programa);
    }

    public List<Asignatura> listarCriticas() {
        return asignaturaRepository.findByCriticaTrue();
    }

    /**
     * Registra una nueva asignatura verificando que el código no esté repetido.
     */
    public Asignatura crear(AsignaturaDto dto) {
        String codigoLimpio = dto.getCodigo().trim().toUpperCase();

        if (asignaturaRepository.existsByCodigo(codigoLimpio)) {
            throw new ReglaNegocioException("Ya existe una asignatura registrada con el código: " + codigoLimpio);
        }

        Asignatura nueva = new Asignatura(
                null,
                codigoLimpio,
                dto.getNombre().trim(),
                dto.getPrograma().trim(),
                dto.isCritica()
        );

        return asignaturaRepository.save(nueva);
    }
}
