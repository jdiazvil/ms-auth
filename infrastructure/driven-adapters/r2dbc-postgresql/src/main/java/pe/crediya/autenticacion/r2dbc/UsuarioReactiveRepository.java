package pe.crediya.autenticacion.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.crediya.autenticacion.r2dbc.entity.UsuarioEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO: This file is just an example, you should delete or modify it
public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, Long>, ReactiveQueryByExampleExecutor<UsuarioEntity> {
    Mono<UsuarioEntity> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Flux<UsuarioEntity> findByEmailIn(List<String> emails);
}
