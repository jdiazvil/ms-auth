package pe.crediya.autenticacion.model.usuario.gateways;

public interface PasswordHasher {
    String encodeContrasena(String contrasena);
    Boolean compareContrasena(String contrasena, String contrasenaEncrip);
}
