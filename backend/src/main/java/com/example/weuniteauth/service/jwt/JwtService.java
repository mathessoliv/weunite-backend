package com.example.weuniteauth.service.jwt;

import com.example.weuniteauth.domain.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    private static final Long DEFAULT_TOKEN_EXPIRATION_SECONDS = 15 * 24 * 60 * 60 * 1000L;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(User user) {
        return generateToken(user, DEFAULT_TOKEN_EXPIRATION_SECONDS);
    }

    public String generateToken(User user, Long expirationInSeconds) {
        Instant now = Instant.now();

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("weunite")
                .subject(user.getId().toString())
                .expiresAt(now.plusMillis(expirationInSeconds))
                .issuedAt(now);

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    public Long getDefaultExpirationTime() {
        return DEFAULT_TOKEN_EXPIRATION_SECONDS;
    }
}
