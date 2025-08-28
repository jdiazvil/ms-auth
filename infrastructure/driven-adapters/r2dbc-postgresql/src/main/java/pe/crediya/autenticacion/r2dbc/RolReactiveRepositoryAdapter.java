package pe.crediya.autenticacion.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import pe.crediya.autenticacion.model.rol.Rol;
import pe.crediya.autenticacion.model.rol.gateways.RolRepository;
import pe.crediya.autenticacion.r2dbc.entity.RolEntity;
import pe.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        Long,
        RolReactiveRepository
> implements RolRepository {
    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }


    @Override
    public Mono<Rol> save(Rol entity) {
        return super.save(entity);
    }

    @Override
    public Mono<Rol> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Mono<Rol> findByNombre(String nombre) {
        return repository.findByNombre(nombre)
                .map(e -> mapper.map(e, Rol.class));
    }

    @Override
    public Flux<Rol> findAll() {
        return super.findAll();
    }

    @Override
    public Mono<Void> deleteById(Long idRol) {
        return repository.deleteById(idRol).then();
    }
}
