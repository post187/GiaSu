package Config.Jwt;

import User.Entity.User;
import User.Repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.openid.connect.sdk.assurance.claims.VerifiedClaimsSet;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class JwtUtil {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.signerAccessKey}")
    private String ACCESS_KEY;

    @Value("${jwt.signerRefreshKey}")
    private String REFRESH_KEY;

    private static final long ACCESS_TOKEN_EXPIRATION = 20 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_KEY, ACCESS_TOKEN_EXPIRATION, "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_KEY, REFRESH_TOKEN_EXPIRATION, "refresh");
    }

    private String generateToken(User user, String Key, long expiration, String type) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("Web")
                .claim("role", user.getRole())   // danh sách roles
                .claim("type", type)               // access / refresh
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(Key));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    // ========================Validation================

    public boolean validateToken(String token, boolean isAccessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            String key = isAccessToken ? ACCESS_KEY : REFRESH_KEY;

            return expiration.after(new Date()) &&
                    verified(token, key);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verified(String token, String key) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(key);

            return signedJWT.verify(jwsVerifier);
        } catch (JOSEException | ParseException e ) {
            throw new RuntimeException(e);
        }
    }

    // ====================== Extract Claims ======================

    public String getEmail(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRole(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return signedJWT.getJWTClaimsSet().getClaim("role").toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
