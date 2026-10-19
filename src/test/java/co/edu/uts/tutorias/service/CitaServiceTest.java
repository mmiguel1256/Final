package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.SolicitudCitaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CitaService citaService;

    private Disponibilidad franjaLibre;
    private Disponibilidad franjaReservada;

    @BeforeEach
    void setUp() {
        franjaLibre = new Disponibilidad(
                "disp-1",
                "docente-1",
                "asig-calc",
                LocalDate.now().plusDays(2),
                LocalTime.of(8, 0),
                LocalTime.of(9, 30),
                Modalidad.PRESENCIAL,
                "Aula 101",
                EstadoDisponibilidad.LIBRE
        );

        franjaReservada = new Disponibilidad(
                "disp-2",
                "docente-1",
                "asig-calc",
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                Modalidad.PRESENCIAL,
                "Aula 101",
                EstadoDisponibilidad.RESERVADA
        );
    }

    @Test
    @DisplayName("Solicitar una franja ya RESERVADA debe fallar")
    void testSolicitarFranjaYaReservadaFalla() {
        when(disponibilidadRepository.findById("disp-2")).thenReturn(Optional.of(franjaReservada));

        SolicitudCitaDto dto = new SolicitudCitaDto("disp-2", "Dudas sobre derivadas compuestas");

        assertThrows(ReglaNegocioException.class, () ->
                citaService.solicitarCita("estudiante-1", dto)
        );
    }

    @Test
    @DisplayName("Estudiante con otra cita activa a la misma hora no puede reservar")
    void testEstudianteConOtraCitaActivaALaMismaHoraNoPuedeReservar() {
        // Franja que se quiere solicitar (8:00 a 9:30)
        when(disponibilidadRepository.findById("disp-1")).thenReturn(Optional.of(franjaLibre));

        // Cita ya activa del estudiante con franja en el mismo horario
        Disponibilidad franjaConflicto = new Disponibilidad(
                "disp-conflicto",
                "docente-2",
                "asig-edat",
                LocalDate.now().plusDays(2),
                LocalTime.of(8, 30), // se cruza
                LocalTime.of(10, 0),
                Modalidad.VIRTUAL,
                "Meet",
                EstadoDisponibilidad.RESERVADA
        );

        Cita citaActiva = new Cita(
                "cita-activa",
                "estudiante-1",
                "disp-conflicto",
                "Taller de árboles",
                EstadoCita.ACEPTADA,
                null,
                LocalDateTime.now().minusDays(1)
        );

        when(citaRepository.findByEstudianteIdAndEstadoIn(eq("estudiante-1"), any()))
                .thenReturn(List.of(citaActiva));
        when(disponibilidadRepository.findById("disp-conflicto")).thenReturn(Optional.of(franjaConflicto));

        SolicitudCitaDto dto = new SolicitudCitaDto("disp-1", "Dudas de cálculo");

        assertThrows(ReglaNegocioException.class, () ->
                citaService.solicitarCita("estudiante-1", dto)
        );
    }

    @Test
    @DisplayName("Rechazar una cita deja la cita en RECHAZADA y la franja en LIBRE")
    void testRechazarCitaDejaCitaEnRechazadaYFranjaEnLibre() {
        Disponibilidad dispAsociada = new Disponibilidad(
                "disp-1",
                "docente-1",
                "asig-calc",
                LocalDate.now().plusDays(2),
                LocalTime.of(8, 0),
                LocalTime.of(9, 30),
                Modalidad.PRESENCIAL,
                "Aula 101",
                EstadoDisponibilidad.RESERVADA
        );

        Cita citaPendiente = new Cita(
                "cita-1",
                "estudiante-1",
                "disp-1",
                "Tema prueba",
                EstadoCita.PENDIENTE,
                null,
                LocalDateTime.now()
        );

        when(citaRepository.findById("cita-1")).thenReturn(Optional.of(citaPendiente));
        when(disponibilidadRepository.findById("disp-1")).thenReturn(Optional.of(dispAsociada));
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.rechazarCita("docente-1", "cita-1", "No podré asistir por reunión");

        assertEquals(EstadoCita.RECHAZADA, resultado.getEstado());
        assertEquals("No podré asistir por reunión", resultado.getComentarioDocente());
        assertEquals(EstadoDisponibilidad.LIBRE, dispAsociada.getEstado());
        verify(disponibilidadRepository).save(dispAsociada);
    }

    @Test
    @DisplayName("Cancelar una cita deja la cita en CANCELADA y la franja en LIBRE")
    void testCancelarCitaDejaCitaEnCanceladaYFranjaEnLibre() {
        Disponibilidad dispAsociada = new Disponibilidad(
                "disp-1",
                "docente-1",
                "asig-calc",
                LocalDate.now().plusDays(2),
                LocalTime.of(8, 0),
                LocalTime.of(9, 30),
                Modalidad.PRESENCIAL,
                "Aula 101",
                EstadoDisponibilidad.RESERVADA
        );

        Cita citaAceptada = new Cita(
                "cita-1",
                "estudiante-1",
                "disp-1",
                "Tema prueba",
                EstadoCita.ACEPTADA,
                null,
                LocalDateTime.now()
        );

        when(citaRepository.findById("cita-1")).thenReturn(Optional.of(citaAceptada));
        when(disponibilidadRepository.findById("disp-1")).thenReturn(Optional.of(dispAsociada));
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.cancelarCita("estudiante-1", "cita-1");

        assertEquals(EstadoCita.CANCELADA, resultado.getEstado());
        assertEquals(EstadoDisponibilidad.LIBRE, dispAsociada.getEstado());
        verify(disponibilidadRepository).save(dispAsociada);
    }
}
