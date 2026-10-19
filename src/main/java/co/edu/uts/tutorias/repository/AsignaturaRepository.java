package co.edu.uts.tutorias.repository;

import co.edu.uts.tutorias.model.Asignatura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignaturaRepository extends MongoRepository<Asignatura, String> {

    Optional<Asignatura> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Asignatura> findByPrograma(String programa);

    List<Asignatura> findByCriticaTrue();
}
