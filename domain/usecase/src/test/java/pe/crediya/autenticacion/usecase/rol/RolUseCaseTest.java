package pe.crediya.autenticacion.usecase.rol;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.crediya.autenticacion.model.rol.gateways.RolRepository;

public class RolUseCaseTest {
    @Mock
    private RolRepository rolRepository;
    private RolUseCase rolUseCase;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        rolUseCase = new RolUseCase(rolRepository);
    }
}
