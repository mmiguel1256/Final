package co.edu.uts.tutorias.repository;

import co.edu.uts.tutorias.model.Disponibilidad;
import co.edu.uts.tutorias.model.EstadoDisponibilidad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisponibilidadRepository extends MongoRepository<Disponibilidad, String> {

    List<Disponibilidad> findByDocenteId(String docenteId);

    List<Disponibilidad> findByAsignaturaIdAndEstado(String asignaturaId, EstadoDisponibilidad estado);

    List<Disponibilidad> findByDocenteIdAndFecha(String docenteId, LocalDate fecha);

    List<Disponibilidad> findByEstado(EstadoDisponibilidad estado);
}
