package pe.crediya.autenticacion.r2dbc;

import pe.crediya.autenticacion.model.usuario.Usuario;
import pe.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import pe.crediya.autenticacion.r2dbc.entity.UsuarioEntity;
import pe.crediya.autenticacion.r2dbc.helper.R2dbcErrorMapper;
import pe.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        Long,
        UsuarioReactiveRepository
> implements UsuarioRepository {
    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Usuario.class));
    }

    @Override
    public Mono<Usuario> save(Usuario entity) {
        return super.save(entity).onErrorMap(R2dbcErrorMapper::toBusiness);
    }

    @Override
    public Mono<Usuario> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Flux<Usuario> findByExample(Usuario entity) {
        return super.findByExample(entity);
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        return repository.findByEmail(email).map(
                e -> mapper.map(e, Usuario.class));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Flux<Usuario> findAll() {
        return super.findAll();
    }

    @Override
    public Mono<Void> deleteById(Long idUsuario) {
        return repository.deleteById(idUsuario)
                .onErrorMap(R2dbcErrorMapper::toBusiness)
                .then();
    }

    @Override
    public Flux<Usuario> findByEmails(List<String> emails) {
        return repository.findByEmailIn(emails)
                .map(e -> mapper.map(e, Usuario.class));
    }
}
