package co.edu.uts.tutorias.repository;

import co.edu.uts.tutorias.model.Cita;
import co.edu.uts.tutorias.model.EstadoCita;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends MongoRepository<Cita, String> {

    List<Cita> findByEstudianteId(String estudianteId);

    List<Cita> findByDisponibilidadId(String disponibilidadId);

    List<Cita> findByDisponibilidadIdIn(List<String> disponibilidadIds);

    List<Cita> findByEstado(EstadoCita estado);

    List<Cita> findByEstudianteIdAndEstadoIn(String estudianteId, List<EstadoCita> estados);
}
