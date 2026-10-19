package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.DisponibilidadDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Modalidad;
import co.edu.uts.tutorias.model.Rol;
import co.edu.uts.tutorias.model.Usuario;
import co.edu.uts.tutorias.repository.DisponibilidadRepository;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    private Usuario docenteConAsignatura;
    private Usuario docenteSinAsignatura;

    @BeforeEach
    void setUp() {
        docenteConAsignatura = new Usuario(
                "docente-1",
                "Prof. Juan Docente",
                "docente1@correo.uts.edu.co",
                Rol.DOCENTE,
                "Ingeniería de Sistemas",
                Arrays.asList("ASIG-CALC"),
                true
        );

        docenteSinAsignatura = new Usuario(
                "docente-2",
                "Prof. Pedro Sin Materias",
                "docente2@correo.uts.edu.co",
                Rol.DOCENTE,
                "Ingeniería de Sistemas",
                Collections.emptyList(),
                true
        );
    }

    @Test
    @DisplayName("Crear una franja con fecha pasada debe fallar")
    void testCrearFranjaConFechaPasadaFalla() {
        when(usuarioRepository.findById("docente-1")).thenReturn(Optional.of(docenteConAsignatura));

        DisponibilidadDto dto = new DisponibilidadDto(
                "ASIG-CALC",
                LocalDate.now().minusDays(1), // fecha pasada
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                Modalidad.PRESENCIAL,
                "Aula 101"
        );

        assertThrows(ReglaNegocioException.class, () ->
                disponibilidadService.crearFranja("docente-1", dto)
        );
    }

    @Test
    @DisplayName("Docente sin la asignatura asignada no puede publicar")
    void testDocenteSinAsignaturaAsignadaNoPuedePublicar() {
        when(usuarioRepository.findById("docente-2")).thenReturn(Optional.of(docenteSinAsignatura));

        DisponibilidadDto dto = new DisponibilidadDto(
                "ASIG-CALC", // materia no asignada al docente-2
                LocalDate.now().plusDays(2),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                Modalidad.PRESENCIAL,
                "Aula 101"
        );

        assertThrows(ReglaNegocioException.class, () ->
                disponibilidadService.crearFranja("docente-2", dto)
        );
    }

    @Test
    @DisplayName("Hora de fin igual o anterior a hora de inicio debe fallar")
    void testHoraFinAnteriorAHoraInicioFalla() {
        when(usuarioRepository.findById("docente-1")).thenReturn(Optional.of(docenteConAsignatura));

        DisponibilidadDto dto = new DisponibilidadDto(
                "ASIG-CALC",
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                LocalTime.of(9, 0), // anterior a hora de inicio
                Modalidad.PRESENCIAL,
                "Aula 101"
        );

        assertThrows(ReglaNegocioException.class, () ->
                disponibilidadService.crearFranja("docente-1", dto)
        );
    }
}
