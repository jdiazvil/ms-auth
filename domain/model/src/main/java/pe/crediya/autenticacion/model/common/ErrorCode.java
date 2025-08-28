package pe.crediya.autenticacion.model.common;

public enum ErrorCode {
    VALIDATION_ERROR,
    DUPLICATE_KEY,          // p.ej. email único
    FOREIGN_KEY_VIOLATION,  // p.ej. id_rol inexistente
    CHECK_VIOLATION,        // violación de constraint
    DATA_INTEGRITY          // genérico de BD
}
