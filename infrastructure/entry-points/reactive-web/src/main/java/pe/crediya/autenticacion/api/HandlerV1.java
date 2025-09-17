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
import pe.crediya.autenticacion.model.usuario.gateways.TokenIssuer;
import pe.crediya.autenticacion.usecase.rol.RolUseCase;
import pe.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerV1 {
    private final UsuarioUseCase usuarioUseCase;
    private final RolUseCase rolUseCase;
    private final TransactionalOperator tx;
    private final TokenIssuer tokenIssuer;

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

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(req -> usuarioUseCase.login(req.email(), req.contrasena()))
                .flatMap( u ->Mono.zip(
                        Mono.just(u),
                        rolUseCase.obtenerPorId(u.getIdRol())
                ))
                .map(tuple -> {
                        var u = tuple.getT1();
                        var rol = tuple.getT2();

                        var roles = List.of(rol.getNombre());
                        var extra = Map.<String,Object>of(
                            "name", u.getNombre() + " " + u.getApellido()
                    );
                    //
                    String token = tokenIssuer.issue(
                            u.getEmail(),
                            roles,
                            extra,
                            java.time.Duration.ofHours(1)
                    );
                    return new LoginResponse(token, 3600L, "Bearer");
                })
                .flatMap(body -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> obtenerUsuariosBulk(ServerRequest request) {
        return request.bodyToMono(EmailsRequest.class)
                .flatMapMany(req -> usuarioUseCase.obtenerPorEmails(req.emails()))
                .collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(list));
    }

    /*
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
    */

    public record LoginRequest(
            String email,
            String contrasena) {}

    public record LoginResponse(String accessToken, long expiresIn, String tokenType) {}

    public record UsuarioRequest(
            String nombre,
            String apellido,
            String correo_electronico,
            String contrasena,
            String documento_identidad,
            LocalDate fecha_nacimiento,
            String direccion,
            String telefono,
            BigDecimal salario_base,
            Long id_rol
    ) {}

    public record EmailsRequest(List<String> emails) {}

    private Usuario toDomain(UsuarioRequest r) {
        return Usuario.builder()
                .nombre(r.nombre())
                .apellido(r.apellido())
                .email(r.correo_electronico())
                .contrasena(r.contrasena())
                .documentoIdentidad(r.documento_identidad())
                .fechaNacimiento(r.fecha_nacimiento())
                .direccion(r.direccion())
                .telefono(r.telefono())
                .salarioBase(r.salario_base())
                .idRol(r.id_rol())
                .build();
    }
}
