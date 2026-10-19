package co.edu.uts.tutorias.service;

import co.edu.uts.tutorias.dto.PublicacionDto;
import co.edu.uts.tutorias.dto.RespuestaDto;
import co.edu.uts.tutorias.exception.ReglaNegocioException;
import co.edu.uts.tutorias.model.Publicacion;
import co.edu.uts.tutorias.repository.AsignaturaRepository;
import co.edu.uts.tutorias.repository.PublicacionRepository;
import co.edu.uts.tutorias.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForoServiceTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ForoService foroService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Título de publicación con menos de 3 caracteres debe fallar")
    void testTituloMenorA3CaracteresFalla() {
        when(asignaturaRepository.existsById("asig-1")).thenReturn(true);
        when(usuarioRepository.existsById("user-1")).thenReturn(true);

        PublicacionDto dto = new PublicacionDto("AB", "Contenido válido suficiente para el foro.");

        assertThrows(ReglaNegocioException.class, () ->
                foroService.crearPublicacion("asig-1", "user-1", dto)
        );
    }

    @Test
    @DisplayName("Contenido de publicación con menos de 5 caracteres debe fallar")
    void testContenidoMenorA5CaracteresFalla() {
        when(asignaturaRepository.existsById("asig-1")).thenReturn(true);
        when(usuarioRepository.existsById("user-1")).thenReturn(true);

        PublicacionDto dto = new PublicacionDto("Título Válido", "Hola");

        assertThrows(ReglaNegocioException.class, () ->
                foroService.crearPublicacion("asig-1", "user-1", dto)
        );
    }

    @Test
    @DisplayName("Respuesta de menos de 2 caracteres debe fallar")
    void testRespuestaMenorA2CaracteresFalla() {
        Publicacion pub = new Publicacion(
                "pub-1", "asig-1", "user-1", "Título Válido", "Contenido largo válido",
                LocalDateTime.now(), new ArrayList<>()
        );

        when(publicacionRepository.findById("pub-1")).thenReturn(Optional.of(pub));
        when(usuarioRepository.existsById("user-2")).thenReturn(true);

        RespuestaDto dto = new RespuestaDto("A");

        assertThrows(ReglaNegocioException.class, () ->
                foroService.agregarRespuesta("pub-1", "user-2", dto)
        );
    }

    @Test
    @DisplayName("Publicar y responder con parámetros válidos debe ser exitoso")
    void testPublicacionYRespuestaValida() {
        when(asignaturaRepository.existsById("asig-1")).thenReturn(true);
        when(usuarioRepository.existsById("user-1")).thenReturn(true);
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(i -> i.getArgument(0));

        PublicacionDto dto = new PublicacionDto("Duda sobre integrales", "¿Cómo se resuelve la integral por partes de x*e^x?");
        Publicacion creada = foroService.crearPublicacion("asig-1", "user-1", dto);

        assertNotNull(creada);
        assertEquals("Duda sobre integrales", creada.getTitulo());

        // Probar responder
        when(publicacionRepository.findById("pub-1")).thenReturn(Optional.of(creada));
        when(usuarioRepository.existsById("docente-1")).thenReturn(true);

        RespuestaDto respDto = new RespuestaDto("Usa u=x y dv=e^x dx.");
        Publicacion conRespuesta = foroService.agregarRespuesta("pub-1", "docente-1", respDto);

        assertEquals(1, conRespuesta.getRespuestas().size());
        assertEquals("Usa u=x y dv=e^x dx.", conRespuesta.getRespuestas().get(0).getTexto());
    }
}
