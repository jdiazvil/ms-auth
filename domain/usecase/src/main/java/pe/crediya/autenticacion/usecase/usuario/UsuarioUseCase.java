package pe.crediya.autenticacion.usecase.usuario;

import lombok.RequiredArgsConstructor;
import pe.crediya.autenticacion.model.common.ErrorCode;
import pe.crediya.autenticacion.model.exception.BusinessException;
import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BigDecimal SALARIO_MIN = BigDecimal.ZERO;
    private static final BigDecimal SALARIO_MAX = new BigDecimal("15000000");

    public Mono<Usuario> crear(Usuario usuario) {
        return Mono.defer(() -> validar(usuario))
                .then(Mono.defer(() -> usuarioRepository.existsByEmail(usuario.getEmail())))
                .flatMap(existe -> existe
                        ? Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Correo electrónico ya registrado"))
                        : usuarioRepository.save(usuario));
    }

    public Mono<Usuario> obtenerPorId(Long id) {
        if (id == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Id de usuario es requerido"));
        return usuarioRepository.findById(id);
    }

    public Mono<Usuario> obtenerPorEmail(String email) {
        if (!isEmail(email)) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Correo electrónico no valido"));
        return usuarioRepository.findByEmail(email);
    }

    public Flux<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Mono<Void> eliminar(Long id) {
        if (id == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Id de usuario es requerido"));
        return usuarioRepository.deleteById(id);
    }

    //Validaciones
    private Mono<Void> validar(Usuario u) {
        if (u == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Usuario es requerido"));
        if (isBlank(u.getNombre()))  return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Nombre es requerido"));
        if (isBlank(u.getApellido()))return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Apellido es requerido"));
        if (!isEmail(u.getEmail()))  return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Correo electronico no valido"));
        if (u.getSalarioBase() == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Salario es requerido"));
        if (u.getSalarioBase().compareTo(SALARIO_MIN) < 0 ||
                u.getSalarioBase().compareTo(SALARIO_MAX) > 0) {
            return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Salario fuera de rango (0..15000000)"));
        }
        return Mono.empty();
    }

    private boolean isEmail(String s) {
        return s != null && EMAIL_REGEX.matcher(s).matches();
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
