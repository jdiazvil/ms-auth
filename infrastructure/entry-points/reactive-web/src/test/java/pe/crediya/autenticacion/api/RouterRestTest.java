package pe.crediya.autenticacion.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.crediya.autenticacion.api.config.TestConfig;
import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, HandlerV1.class, HandlerV2.class, TestConfig.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UsuarioUseCase usuarioUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearUsuario() {
        Usuario usuarioCreado = new Usuario(1L, "Joseph Alfredo", "Diaz Vilchez", "jdiazvil@gmail.com",
                "47042138", LocalDate.parse("2025-08-25"), "Calle S/N", "921018564", BigDecimal.valueOf(1000), 1L);

        when(usuarioUseCase.crear(any(Usuario.class)))
                .thenReturn(Mono.just(usuarioCreado));

        // El cuerpo de la petición que se enviará
        String requestBody = """
            {
                "nombre": "Joseph Alfredo",
                "apellido": "Diaz Vilchez",
                "correo_electronico": "jdiazvil@gmail.com",
                "documento_identidad": "47042138",
                "fecha_nacimiento": "2025-08-25",
                "direccion": "Calle S/N",
                "telefono": "921018564",
                "salario_base": 1000.00,
                "id_rol": 1
            }
            """;

        // Realizamos la petición POST
        webTestClient.post()
                .uri("/api/v1/usuarios") // URI del endpoint
                .contentType(MediaType.APPLICATION_JSON) // Tipo de contenido JSON
                .bodyValue(requestBody) // Cuerpo de la petición
                .exchange() // Ejecutamos la petición
                .expectStatus().isCreated() // Esperamos que la respuesta sea 201 (Creado)
                .expectHeader().exists("Location") // Verificamos que el encabezado Location esté presente
                .expectBody() // Verificamos el cuerpo de la respuesta
                .jsonPath("$.idUsuario").isEqualTo(1) // Verificamos que el idUsuario sea el esperado
                .jsonPath("$.nombre").isEqualTo("Joseph Alfredo") // Verificamos el nombre
                .jsonPath("$.apellido").isEqualTo("Diaz Vilchez"); // Verificamos el apellido
    }

    /*
    @Test
    void testListenGETUseCaseV1() {
        webTestClient.get()
                .uri("/api/v1/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
    @Test
    void testListenGETUseCaseV2() {
        webTestClient.get()
                .uri("/api/v2/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCaseV1() {
        webTestClient.get()
                .uri("/api/v1/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
    @Test
    void testListenGETOtherUseCaseV2() {
        webTestClient.get()
                .uri("/api/v2/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenPOSTUseCaseV1() {
        webTestClient.post()
                .uri("/api/v1/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
    @Test
    void testListenPOSTUseCaseV2() {
        webTestClient.post()
                .uri("/api/v2/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
     */
}
