package pe.crediya.autenticacion.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import pe.crediya.autenticacion.api.config.CorrelationIdWebFilter;
import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerV1 {
    private final UsuarioUseCase usuarioUseCase;
    private final TransactionalOperator tx;

    // HU-1: Registrar usuario
    public Mono<ServerResponse> crearUsuario(ServerRequest request){
        return request.bodyToMono(UsuarioRequest.class)
                .map(this::toDomain)
                .flatMap( u -> usuarioUseCase.crear(u).as(tx::transactional))
                .flatMap(u -> Mono.deferContextual(ctx -> {
                    String cid = ctx.getOrDefault(CorrelationIdWebFilter.CONTEXT_KEY, "n/a");
                    log.info("usuario_creado cid={} id={} email={}", cid, u.getIdUsuario(), u.getEmail());
                    return ServerResponse.created(URI.create("/api/v1/usuarios/" + u.getIdUsuario()))
                            .contentType(APPLICATION_JSON)
                            .bodyValue(u);
                }));
                /*
                .onErrorResume(DuplicateKeyException.class, e ->
                        ServerResponse.status(CONFLICT).contentType(APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("CONFLICT", "Correo electrónico ya registrado")) )
                .onErrorResume(IllegalArgumentException.class,
                        e -> ServerResponse.badRequest().contentType(APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("VALIDATION_ERROR", e.getMessage())))
                .onErrorResume(IllegalStateException.class,
                        e -> ServerResponse.status(CONFLICT).contentType(APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("CONFLICT", e.getMessage())));
                 */

    }

    @PreAuthorize("hasRole('permissionGET')")
    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    @PreAuthorize("hasRole('permissionGETOther')")
    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    @PreAuthorize("hasRole('permissionPOST')")
    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public record UsuarioRequest(
            String nombre,
            String apellido,
            String correo_electronico,
            String documento_identidad,
            LocalDate fecha_nacimiento,
            String direccion,
            String telefono,
            BigDecimal salario_base,
            Long id_rol
    ) {}

    private Usuario toDomain(UsuarioRequest r) {
        return Usuario.builder()
                .nombre(r.nombre())
                .apellido(r.apellido())
                .email(r.correo_electronico())
                .documentoIdentidad(r.documento_identidad())
                .fechaNacimiento(r.fecha_nacimiento())
                .direccion(r.direccion())
                .telefono(r.telefono())
                .salarioBase(r.salario_base())
                .idRol(r.id_rol())
                .build();
    }
}
