package co.edu.uts.tutorias.repository;

import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByPrograma(String programa);

    List<Usuario> findByRolAndPrograma(Rol rol, String programa);
}
