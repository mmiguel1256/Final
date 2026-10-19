package co.edu.uts.tutorias.repository;

import co.edu.uts.tutorias.model.Publicacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends MongoRepository<Publicacion, String> {

    List<Publicacion> findByAsignaturaIdOrderByFechaDesc(String asignaturaId);

    List<Publicacion> findAllByOrderByFechaDesc();
}
