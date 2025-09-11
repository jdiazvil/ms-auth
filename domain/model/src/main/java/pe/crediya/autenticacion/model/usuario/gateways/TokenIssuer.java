package pe.crediya.autenticacion.model.usuario.gateways;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface TokenIssuer {
    String issue(
            String subject,
            List<String> roles,
            Map<String,Object> extra,
            Duration ttl);
}
