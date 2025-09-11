package pe.crediya.autenticacion.model.usuario;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private BigDecimal salarioBase;
    private Long idRol;
}
