package pe.crediya.autenticacion.usecase.usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.crediya.autenticacion.model.common.ErrorCode;
import pe.crediya.autenticacion.model.exception.BusinessException;
import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsuarioUseCaseTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    private UsuarioUseCase useCase;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        useCase = new UsuarioUseCase(usuarioRepository);
    }

    // ---------- crear()
    @Test
    @DisplayName("crear(): usuario válido → guarda y retorna persistido")
    void crear_ok() {
        Usuario entrada = usuarioValido(null, "ana@demo.com", new BigDecimal("1200"));
        Usuario persistido = usuarioValido(1L, "ana@demo.com", new BigDecimal("1200"));

        when(usuarioRepository.existsByEmail("ana@demo.com")).thenReturn(Mono.just(false));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(persistido));

        StepVerifier.create(useCase.crear(entrada))
                .assertNext(u -> {
                    assertNotNull(u.getIdUsuario());
                    assertEquals("ana@demo.com", u.getEmail());
                    assertEquals("Ana", u.getNombre());
                })
                .verifyComplete();

        verify(usuarioRepository).existsByEmail("ana@demo.com");
        verify(usuarioRepository).save(any(Usuario.class));
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("crear(): email ya registrado → BusinessException")
    void crear_emailDuplicado() {
        Usuario entrada = usuarioValido(null, "ana@demo.com", new BigDecimal("1200"));

        when(usuarioRepository.existsByEmail("ana@demo.com")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.crear(entrada))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof BusinessException);
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("correo"));
                })
                .verify();

        verify(usuarioRepository).existsByEmail("ana@demo.com");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("crear(): salario fuera de rango → BusinessException")
    void crear_salarioFueraRango() {
        Usuario entrada = usuarioValido(null, "ana@demo.com", new BigDecimal("-1"));

        StepVerifier.create(useCase.crear(entrada))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof BusinessException);
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("salario"));
                })
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("crear(): nombre/apellido vacíos → BusinessException")
    void crear_camposVacios() {
        Usuario sinNombre = usuarioValido(null, "ana@demo.com", new BigDecimal("1200"));
        sinNombre.setNombre("   ");
        StepVerifier.create(useCase.crear(sinNombre))
                .expectErrorSatisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
                })
                .verify();

        Usuario sinApellido = usuarioValido(null, "ana@demo.com", new BigDecimal("1200"));
        sinApellido.setApellido("");
        StepVerifier.create(useCase.crear(sinApellido))
                .expectErrorSatisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("apellido"));
                })
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("crear(): email inválido o salario null → BusinessException")
    void crear_emailInvalidoOSalarioNull() {
        Usuario emailBad = usuarioValido(null, "ana#demo.com", new BigDecimal("1200"));
        StepVerifier.create(useCase.crear(emailBad))
                .expectErrorSatisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("correo"));
                })
                .verify();

        Usuario salarioNull = usuarioValido(null, "ana@demo.com", null);
        StepVerifier.create(useCase.crear(salarioNull))
                .expectErrorSatisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("salario"));
                })
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    // ---------- obtenerPorId()
    @Test
    @DisplayName("obtenerPorId(): id null → BusinessException")
    void obtenerPorId_idNull() {
        StepVerifier.create(useCase.obtenerPorId(null))
                .expectErrorSatisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(ex.getMessage().toLowerCase().contains("id de usuario"));
                })
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("obtenerPorId(): id válido → delega al repositorio")
    void obtenerPorId_ok() {
        Usuario u = usuarioValido(1L, "ana@demo.com", new BigDecimal("1200"));
        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(u));

        StepVerifier.create(useCase.obtenerPorId(1L))
                .expectNextMatches(x -> x.getIdUsuario().equals(1L))
                .verifyComplete();

        verify(usuarioRepository).findById(1L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    // ---------- obtenerPorEmail()
    @Test
    @DisplayName("obtenerPorEmail(): email inválido → BusinessException")
    void obtenerPorEmail_invalido() {
        StepVerifier.create(useCase.obtenerPorEmail("ana#demo.com"))
                .expectErrorSatisfies(ex -> {
                    assertInstanceOf(BusinessException.class, ex);
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                    assertTrue(be.getMessage().toLowerCase().contains("correo"));
                })
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("obtenerPorEmail(): válido → delega al repositorio")
    void obtenerPorEmail_ok() {
        Usuario u = usuarioValido(1L, "ana@demo.com", new BigDecimal("1200"));
        when(usuarioRepository.findByEmail("ana@demo.com")).thenReturn(Mono.just(u));

        StepVerifier.create(useCase.obtenerPorEmail("ana@demo.com"))
                .expectNext(u)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("ana@demo.com");
        verifyNoMoreInteractions(usuarioRepository);
    }

    // ---------- listar()
    @Test
    @DisplayName("listar(): retorna flujo del repositorio")
    void listar_ok() {
        when(usuarioRepository.findAll())
                .thenReturn(Flux.just(
                        usuarioValido(1L, "a@a.com", new BigDecimal("1000")),
                        usuarioValido(2L, "b@b.com", new BigDecimal("2000"))
                ));

        StepVerifier.create(useCase.listar())
                .expectNextCount(2)
                .verifyComplete();

        verify(usuarioRepository).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    // ---------- eliminar()
    @Test
    @DisplayName("eliminar(): id null → BusinessException")
    void eliminar_idNull() {
        StepVerifier.create(useCase.eliminar(null))
                .expectErrorSatisfies(ex -> {
                    assertInstanceOf(BusinessException.class, ex);
                    BusinessException be = (BusinessException) ex;
                    assertEquals(ErrorCode.VALIDATION_ERROR, be.getCode());
                })
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("eliminar(): id válido → delega al repositorio y completa")
    void eliminar_ok() {
        when(usuarioRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.eliminar(1L))
                .verifyComplete();

        verify(usuarioRepository).deleteById(1L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    // ---------- helpers
    private Usuario usuarioValido(Long id, String email, BigDecimal salario) {
        return new Usuario(
                id,                 // id_usuario
                "Ana",              // nombre
                "Paz",              // apellido
                email,              // email
                null, null, null, null, // documento, fecha, direccion, telefono
                salario,            // salario_base
                1L                // id_rol
        );
    }


}
