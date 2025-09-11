package pe.crediya.autenticacion.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pe.crediya.autenticacion.model.usuario.gateways.PasswordHasher;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encodeContrasena(String contrasena) {
        return encoder.encode(contrasena);
    }

    @Override
    public Boolean compareContrasena(String contrasena, String contrasenaEncrip) {
        return encoder.matches(contrasena,contrasenaEncrip);
    }
}
