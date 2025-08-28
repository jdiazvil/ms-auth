package pe.crediya.autenticacion.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.crediya.autenticacion.model.usuario.Usuario;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;


@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = RequestMethod.POST,
                    beanClass = HandlerV1.class,
                    beanMethod = "crearUsuario",
                    operation = @Operation(
                            operationId = "crearUsuario",
                            summary = "Registrar nuevo usuario (solo ADMIN)",
                            description = "Crea un usuario solicitante. Requiere rol ADMIN.",
                            security = { @SecurityRequirement(name = "bearer-jwt") },
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = HandlerV1.UsuarioRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Creado",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "401", description = "No autenticado"),
                                    @ApiResponse(responseCode = "403", description = "Sin rol ADMIN"),
                                    @ApiResponse(responseCode = "409", description = "Email duplicado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerV1 handlerV1, HandlerV2 handlerV2) {
        return RouterFunctions.route()
            .path("/api/v1", builder -> builder
                    .POST("/usuarios", accept(APPLICATION_JSON),handlerV1::crearUsuario)
            )
            /*
            .path("/api/v2", builder -> builder
                    .GET("/usecase/path", handlerV2::listenGETUseCase)
                    .POST("/usecase/otherpath", handlerV2::listenPOSTUseCase)
                    .GET("/otherusercase/path", handlerV2::listenGETOtherUseCase))
             */
            .build();
    }
}
