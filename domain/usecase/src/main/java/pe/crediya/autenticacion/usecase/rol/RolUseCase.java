package pe.crediya.autenticacion.usecase.rol;

import lombok.RequiredArgsConstructor;
import pe.crediya.autenticacion.model.common.ErrorCode;
import pe.crediya.autenticacion.model.exception.BusinessException;
import pe.crediya.autenticacion.model.rol.Rol;
import pe.crediya.autenticacion.model.rol.gateways.RolRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolUseCase {
    private final RolRepository rolRepository;

    public Mono<Rol> crear(Rol rol) {
        if (rol == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Rol es requerido"));
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Nombre de rol es requerido"));
        }
        return rolRepository.findByNombre(rol.getNombre())
                .flatMap(r -> Mono.<Rol>error(new BusinessException(ErrorCode.VALIDATION_ERROR,"El rol ya existe")))
                .switchIfEmpty(rolRepository.save(rol));
    }

    public Mono<Rol> obtenerPorId(Long idRol) {
        if (idRol == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Id de rol es requerido"));
        return rolRepository.findById(idRol);
    }

    public Flux<Rol> listar() {
        return rolRepository.findAll();
    }

    public Mono<Void> eliminar(Long idRol) {
        if (idRol == null) return Mono.error(new BusinessException(ErrorCode.VALIDATION_ERROR,"Id de rol es requerido"));
        return rolRepository.deleteById(idRol);
    }
}
