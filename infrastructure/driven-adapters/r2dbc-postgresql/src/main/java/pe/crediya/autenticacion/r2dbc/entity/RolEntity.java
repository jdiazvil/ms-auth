package pe.crediya.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("rol")
public class RolEntity {
    @Id
    @Column("id_rol")
    private Long idRol;

    @Column("nombre")
    private String nombre;

    @Column("descripcion")
    private String descripcion;
}
