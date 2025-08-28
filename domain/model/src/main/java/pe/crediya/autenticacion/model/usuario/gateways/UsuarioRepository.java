package pe.crediya.autenticacion.model.usuario.gateways;

import pe.crediya.autenticacion.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario usuario);
    Mono<Usuario> findById(Long idUsuario);
    Mono<Usuario> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Flux<Usuario> findAll();
    Mono<Void> deleteById(Long idUsuario);
}
