package pe.crediya.autenticacion;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Disabled("Auth se implementa en Semana 02")
public class UsuariosEndpointSecurityIT {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    @Test
    @DisplayName("Sin JWT → 401")
    void postUsuarios_sinJwt_401() {
        webTestClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                { "nombre":"Ana", "apellido":"Paz", "email":"ana@demo.com", "salarioBase":1200.00 }
            """)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("JWT sin rol ADMIN → 403")
    void postUsuarios_jwtSinAdmin_403() {
        webTestClient
                .mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_SOLICITANTE")))
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                { "nombre":"Ana", "apellido":"Paz", "email":"ana@demo.com", "salarioBase":1200.00 }
            """)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("JWT con rol ADMIN → 201")
    void postUsuarios_jwtAdmin_201() {
        Usuario creado = new Usuario(1L, "Ana", "Paz", "ana@demo.com",null,
                null,null,null,null, BigDecimal.valueOf(1200), null);

        given(usuarioUseCase.crear(any())).willReturn(Mono.just(creado));

        webTestClient
                .mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                { "nombre":"Ana", "apellido":"Paz", "email":"ana@demo.com", "salarioBase":1200.00 }
            """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location");
    }

    @Test
    @DisplayName("JWT ADMIN → 409 cuando el use case informa email duplicado")
    void postUsuarios_jwtAdmin_409_conflict() {
        given(usuarioUseCase.crear(any()))
                .willReturn(Mono.error(new IllegalStateException("Correo electrónico ya registrado")));

        webTestClient
                .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
          { "nombre":"Ana", "apellido":"Paz", "email":"ana@demo.com", "salarioBase":1200.00 }
      """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectHeader().doesNotExist("Location");
    }

    @Test
    @DisplayName("JWT ADMIN → 400 cuando el use case valida salario fuera de rango")
    void postUsuarios_jwtAdmin_400_badRequest() {
        given(usuarioUseCase.crear(any()))
                .willReturn(Mono.error(new IllegalArgumentException("Salario fuera de rango (0..15000000)")));

        webTestClient
                .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
          { "nombre":"Ana", "apellido":"Paz", "email":"ana@demo.com", "salarioBase":-1 }
      """)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
