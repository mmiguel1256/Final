package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.EstudianteRiesgoDto;
import co.edu.uts.tutorias.dto.ReporteAsignaturaEstadoDto;
import co.edu.uts.tutorias.model.*;
import co.edu.uts.tutorias.repository.AsignaturaRepository;
import co.edu.uts.tutorias.repository.CitaRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Asignatura asigSistemas;
    private Asignatura asigIndustrial;

    @BeforeEach
    void setUp() {
        asigSistemas = new Asignatura("asig-1", "CALC", "Cálculo", "Ingeniería de Sistemas", true);
        asigIndustrial = new Asignatura("asig-2", "FMEC", "Física Mecánica", "Ingeniería Industrial", true);
    }

    @Test
    @DisplayName("El Director solo ve asignaturas de su programa")
    void testDirectorSoloVeAsignaturasDeSuPrograma() {
        when(asignaturaRepository.findAll()).thenReturn(List.of(asigSistemas, asigIndustrial));
        when(disponibilidadRepository.findAll()).thenReturn(Collections.emptyList());
        when(citaRepository.findAll()).thenReturn(Collections.emptyList());

        // Director de Sistemas
        List<ReporteAsignaturaEstadoDto> reporteDirector = reporteService.obtenerReportePorAsignaturaYEstado(
                Rol.DIRECTOR, "Ingeniería de Sistemas"
        );

        assertEquals(1, reporteDirector.size());
        assertEquals("CALC", reporteDirector.get(0).getCodigo());

        // Administrador ve todas las materias de ambos programas
        List<ReporteAsignaturaEstadoDto> reporteAdmin = reporteService.obtenerReportePorAsignaturaYEstado(
                Rol.ADMIN, null
        );

        assertEquals(2, reporteAdmin.size());
    }

    @Test
    @DisplayName("Identificar estudiantes sin citas en asignaturas críticas")
    void testIdentificarEstudiantesSinCitasEnCriticas() {
        when(asignaturaRepository.findByCriticaTrue()).thenReturn(List.of(asigSistemas));

        Disponibilidad dispCritica = new Disponibilidad(
                "disp-critica", "doc-1", "asig-1",
                LocalDate.now().plusDays(2), LocalTime.of(8, 0), LocalTime.of(10, 0),
                Modalidad.PRESENCIAL, "Aula", EstadoDisponibilidad.RESERVADA
        );
        when(disponibilidadRepository.findAll()).thenReturn(List.of(dispCritica));

        // Estudiante 1 tiene cita en la materia crítica
        Cita cita = new Cita("cita-1", "est-1", "disp-critica", "Tema", EstadoCita.ACEPTADA, null, LocalDateTime.now());
        when(citaRepository.findAll()).thenReturn(List.of(cita));

        // Estudiantes activos
        Usuario est1 = new Usuario("est-1", "Estudiante Con Cita", "est1@correo.uts.edu.co", Rol.ESTUDIANTE, "Ingeniería de Sistemas", new ArrayList<>(), true);
        Usuario est2 = new Usuario("est-2", "Estudiante En Riesgo", "est2@correo.uts.edu.co", Rol.ESTUDIANTE, "Ingeniería de Sistemas", new ArrayList<>(), true);

        when(usuarioRepository.findByRol(Rol.ESTUDIANTE)).thenReturn(List.of(est1, est2));

        List<EstudianteRiesgoDto> riesgo = reporteService.obtenerEstudiantesSinCitasEnCriticas(Rol.ADMIN, null);

        assertEquals(1, riesgo.size());
        assertEquals("Estudiante En Riesgo", riesgo.get(0).getNombre());
        assertEquals("est2@correo.uts.edu.co", riesgo.get(0).getCorreo());
    }
}
