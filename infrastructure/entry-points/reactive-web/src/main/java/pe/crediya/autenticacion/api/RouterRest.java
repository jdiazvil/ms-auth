package pe.crediya.autenticacion.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = HandlerV1.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "loginUsuario",
                            summary = "Autenticación de usuario",
                            description = "Valida las credenciales (email y contraseña) y devuelve un token JWT con la información del rol y datos adicionales.",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = HandlerV1.LoginRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                                            content = @Content(schema = @Schema(implementation = HandlerV1.LoginResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
                                    @ApiResponse(responseCode = "500", description = "Error interno")
                            }
                    )
            ),
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
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/bulk",
                    method = RequestMethod.POST,
                    beanClass = HandlerV1.class,
                    beanMethod = "obtenerUsuariosBulk",
                    operation = @Operation(
                            operationId = "obtenerUsuariosBulk",
                            summary = "Obtener usuarios en lote (bulk)",
                            description = "Recupera múltiples usuarios filtrados por una lista de correos electrónicos.",
                            security = { @SecurityRequirement(name = "bearer-jwt") },
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = HandlerV1.EmailsRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class)))),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "401", description = "No autenticado"),
                                    @ApiResponse(responseCode = "404", description = "No se encontraron usuarios")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/obtener-usuario/{email}",
                    method = RequestMethod.GET,
                    beanClass = HandlerV1.class,
                    beanMethod = "obtenerUsuarioPorEmail",
                    operation = @Operation(
                            operationId = "obtenerUsuarioPorEmail",
                            summary = "Obtener un usuario por email",
                            description = "Recupera los detalles de un usuario utilizando su correo electrónico.",
                            parameters = @Parameter(
                                    name = "email",
                                    description = "Correo electrónico del usuario",
                                    required = true,
                                    schema = @Schema(type = "string")
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerV1 handlerV1, HandlerV2 handlerV2) {
        return RouterFunctions.route()
            .path("/api/v1", builder -> builder
                    .POST("/login",accept(APPLICATION_JSON),handlerV1::login)
                    .POST("/usuarios", accept(APPLICATION_JSON),handlerV1::crearUsuario)
                    .POST("/usuarios/bulk",accept(APPLICATION_JSON),handlerV1::obtenerUsuariosBulk)
                    .GET("/usuario/{email}", handlerV1::obtenerUsuarioPorEmail)

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
