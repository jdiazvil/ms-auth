package pe.crediya.autenticacion.model.rol.gateways;

import pe.crediya.autenticacion.model.rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> save(Rol rol);
    Mono<Rol> findById(Long idRol);
    Mono<Rol> findByNombre(String nombre);
    Flux<Rol> findAll();
    Mono<Void> deleteById(Long idRol);
}
