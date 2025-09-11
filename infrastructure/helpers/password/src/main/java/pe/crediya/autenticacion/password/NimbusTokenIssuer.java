package pe.crediya.autenticacion.password;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import pe.crediya.autenticacion.model.usuario.gateways.TokenIssuer;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class NimbusTokenIssuer implements TokenIssuer {
    private final JwtEncoder encoder;
    private final String issuer;

    public NimbusTokenIssuer(
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.secret}") String secret) {

        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.issuer = issuer;
    }

    @Override
    public String issue(String subject, List<String> roles, Map<String, Object> extra, Duration ttl) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .subject(subject)
                .claim("roles", roles);

        if (extra != null && !extra.isEmpty()) {
            extra.forEach(claims::claim);
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(
                header, claims.build())).getTokenValue();
    }
}
