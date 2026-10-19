package co.edu.uts.tutorias.config;

import co.edu.uts.tutorias.model.*;
import co.edu.uts.tutorias.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Carga de datos iniciales de prueba para la plataforma de tutorías UTS.
 * Solo se ejecuta si la colección de usuarios está vacía.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final PublicacionRepository publicacionRepository;

    // Inyección de dependencias por constructor
    public DataLoader(UsuarioRepository usuarioRepository,
                      AsignaturaRepository asignaturaRepository,
                      DisponibilidadRepository disponibilidadRepository,
                      PublicacionRepository publicacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.publicacionRepository = publicacionRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // 1. Cargar 4 asignaturas (al menos 2 críticas, de dos programas distintos)
        Asignatura asig1 = new Asignatura(null, "CALC-01", "Cálculo Diferencial", "Ingeniería de Sistemas", true);
        Asignatura asig2 = new Asignatura(null, "EDAT-02", "Estructuras de Datos", "Ingeniería de Sistemas", true);
        Asignatura asig3 = new Asignatura(null, "FMEC-03", "Física Mecánica", "Ingeniería Industrial", false);
        Asignatura asig4 = new Asignatura(null, "IOP-04", "Investigación de Operaciones", "Ingeniería Industrial", false);

        asig1 = asignaturaRepository.save(asig1);
        asig2 = asignaturaRepository.save(asig2);
        asig3 = asignaturaRepository.save(asig3);
        asig4 = asignaturaRepository.save(asig4);

        // 2. Cargar usuarios (1 por cada rol y 2 estudiantes adicionales)
        Usuario admin = new Usuario(null, "Administrador UTS", "admin@correo.uts.edu.co",
                Rol.ADMIN, "Ingeniería de Sistemas", new ArrayList<>(), true);

        Usuario docente = new Usuario(null, "Prof. Roberto Gómez", "docente1@correo.uts.edu.co",
                Rol.DOCENTE, "Ingeniería de Sistemas", Arrays.asList(asig1.getId(), asig2.getId()), true);

        Usuario monitor = new Usuario(null, "Mario Monitor", "monitor1@correo.uts.edu.co",
                Rol.MONITOR, "Ingeniería de Sistemas", Arrays.asList(asig1.getId()), true);

        Usuario coordinador = new Usuario(null, "Dra. Patricia Silva", "coordinador@correo.uts.edu.co",
                Rol.COORDINADOR, "Ingeniería de Sistemas", new ArrayList<>(), true);

        Usuario director = new Usuario(null, "Dr. Alfonso Valbuena", "director@correo.uts.edu.co",
                Rol.DIRECTOR, "Ingeniería de Sistemas", new ArrayList<>(), true);

        Usuario bienestar = new Usuario(null, "Lic. Claudia Bienestar", "bienestar@correo.uts.edu.co",
                Rol.BIENESTAR, "Bienestar Institucional", new ArrayList<>(), true);

        Usuario estudiante1 = new Usuario(null, "Andrés Felipe Pérez", "estudiante1@correo.uts.edu.co",
                Rol.ESTUDIANTE, "Ingeniería de Sistemas", new ArrayList<>(), true);

        Usuario estudiante2 = new Usuario(null, "Beatriz Elena Morales", "estudiante2@correo.uts.edu.co",
                Rol.ESTUDIANTE, "Ingeniería de Sistemas", new ArrayList<>(), true);

        Usuario estudiante3 = new Usuario(null, "Camilo Torres Duarte", "estudiante3@correo.uts.edu.co",
                Rol.ESTUDIANTE, "Ingeniería Industrial", new ArrayList<>(), true);

        admin = usuarioRepository.save(admin);
        docente = usuarioRepository.save(docente);
        monitor = usuarioRepository.save(monitor);
        coordinador = usuarioRepository.save(coordinador);
        director = usuarioRepository.save(director);
        bienestar = usuarioRepository.save(bienestar);
        estudiante1 = usuarioRepository.save(estudiante1);
        estudiante2 = usuarioRepository.save(estudiante2);
        estudiante3 = usuarioRepository.save(estudiante3);

        // 3. Crear varias franjas de disponibilidad futuras calculadas con plusDays
        Disponibilidad franja1 = new Disponibilidad(null, docente.getId(), asig1.getId(),
                LocalDate.now().plusDays(2), LocalTime.of(8, 0), LocalTime.of(9, 30),
                Modalidad.PRESENCIAL, "Edificio B - Aula 204", EstadoDisponibilidad.LIBRE);

        Disponibilidad franja2 = new Disponibilidad(null, docente.getId(), asig2.getId(),
                LocalDate.now().plusDays(3), LocalTime.of(14, 0), LocalTime.of(15, 30),
                Modalidad.VIRTUAL, "Google Meet: meet.google.com/uts-docente1", EstadoDisponibilidad.LIBRE);

        Disponibilidad franja3 = new Disponibilidad(null, monitor.getId(), asig1.getId(),
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 30),
                Modalidad.PRESENCIAL, "Laboratorio de Cómputo 3", EstadoDisponibilidad.LIBRE);

        disponibilidadRepository.save(franja1);
        disponibilidadRepository.save(franja2);
        disponibilidadRepository.save(franja3);

        // 4. Crear publicación de ejemplo en el foro
        List<Respuesta> respuestasEjemplo = new ArrayList<>();
        respuestasEjemplo.add(new Respuesta(docente.getId(),
                "Hola Andrés, revisaremos la regla de la cadena y varios ejercicios prácticos en la franja del jueves.",
                LocalDateTime.now().minusHours(2)));

        Publicacion publicacionEjemplo = new Publicacion(null, asig1.getId(), estudiante1.getId(),
                "Dudas con la regla de la cadena para derivadas compuestas",
                "Buenas tardes profesor, quisiera saber si en la próxima tutoría podemos repasar derivadas trigonométricas con regla de la cadena.",
                LocalDateTime.now().minusHours(5), respuestasEjemplo);

        publicacionRepository.save(publicacionEjemplo);
    }
}
