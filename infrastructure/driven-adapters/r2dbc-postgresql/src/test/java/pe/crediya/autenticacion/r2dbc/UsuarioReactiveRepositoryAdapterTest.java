package pe.crediya.autenticacion.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.r2dbc.entity.UsuarioEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UsuarioReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UsuarioReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {

        UsuarioEntity usuarioEntity = new UsuarioEntity(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564",
                BigDecimal.valueOf(1000), 1L);

        when(repository.findById(1L)).thenReturn(Mono.just(usuarioEntity));
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(
                new Usuario(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                        "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L)
        );

        Mono<Usuario> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(
                        mapper.map(usuarioEntity,Usuario.class)
                ))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {

        UsuarioEntity usuarioEntity = new UsuarioEntity(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564",
                BigDecimal.valueOf(1000), 1L);


        when(repository.findAll()).thenReturn(Flux.just(usuarioEntity));
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(
                new Usuario(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                        "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L)
        );

        Flux<Usuario> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(
                                mapper.map(usuarioEntity,Usuario.class)
                ))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {

        UsuarioEntity usuarioEntity = new UsuarioEntity(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564",
                BigDecimal.valueOf(1000), 1L);

        Usuario usuario = new Usuario(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L);

        Example<UsuarioEntity> example = Example.of(usuarioEntity);
        when(repository.findAll(example)).thenReturn(Flux.just(usuarioEntity));
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(usuario);
        when(mapper.map(usuario, UsuarioEntity.class)).thenReturn(usuarioEntity);

        Flux<Usuario> result = repositoryAdapter.findByExample(usuario);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(usuario))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {

        UsuarioEntity usuarioEntity = new UsuarioEntity(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564",
                BigDecimal.valueOf(1000), 1L);

        when(repository.save(usuarioEntity)).thenReturn(Mono.just(usuarioEntity));

        Usuario usuario = new Usuario(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L);
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(usuario);
        when(mapper.map(usuario, UsuarioEntity.class)).thenReturn(usuarioEntity);

        Mono<Usuario> result = repositoryAdapter.save(usuario);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(
                        usuario
                ))
                .verifyComplete();
    }

    @Test
    void mustFindByEmail() {
        UsuarioEntity usuarioEntity = new UsuarioEntity(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L);

        when(repository.findByEmail("jdiazvil@gmail.com")).thenReturn(Mono.just(usuarioEntity));

        Usuario usuario = new Usuario(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",null,
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L);
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(usuario);

        Mono<Usuario> result = repositoryAdapter.findByEmail("jdiazvil@gmail.com");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(usuario))
                .verifyComplete();
    }

    @Test
    void mustExistByEmail() {
        when(repository.existsByEmail("jdiazvil@gmail.com")).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existsByEmail("jdiazvil@gmail.com");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void mustNotExistByEmail() {
        when(repository.existsByEmail("nonexistentemail@gmail.com")).thenReturn(Mono.just(false));

        Mono<Boolean> result = repositoryAdapter.existsByEmail("nonexistentemail@gmail.com");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

}
