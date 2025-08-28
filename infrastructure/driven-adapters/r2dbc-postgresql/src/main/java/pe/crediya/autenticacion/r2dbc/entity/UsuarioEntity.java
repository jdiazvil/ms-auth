package pe.crediya.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("usuario")
public class UsuarioEntity {
    @Id
    @Column("id_usuario")
    private Long idUsuario;

    @Column("nombre")
    private String nombre;

    @Column("apellido")
    private String apellido;

    @Column("email")
    private String email;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column("direccion")
    private String direccion;

    @Column("telefono")
    private String telefono;

    @Column("salario_base")
    private BigDecimal salarioBase;

    @Column("id_rol")
    private Long idRol;
}
