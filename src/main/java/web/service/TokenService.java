package web.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import web.db.UserDB;
import web.db.dto.UserDTO;
import web.domain.Token;
import web.domain.TokenPair;
import web.exceptions.MalformedTokenException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Stateful(name = "tokenService")
public class TokenService {
    private static final String SECRET =
            "d2Zza2VqeG93bmdwa3drbWFzbGtma3NhbWZsa3NrYWxrZm1rbGFmbWtsYWZsa2Fz";
    private static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(5);
    private static final Duration REFRESH_EXPIRATION_TIME = Duration.ofDays(30);
    private static final String ISSUER = "web";

    private final SecretKey key;

    @EJB
    private UserDB userDB;

    public TokenService() {
        key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public TokenPair generateTokenPair(UserDTO user) {
        Date creationDate = new Date();
        Date accessExpirationDate = new Date(creationDate.getTime() + ACCESS_EXPIRATION_TIME.toMillis());
        Date refreshExpirationDate = new Date(creationDate.getTime() + REFRESH_EXPIRATION_TIME.toMillis());

        return new TokenPair(ISSUER, user.getUsername(), creationDate,
                accessExpirationDate, refreshExpirationDate);
    }

    public String generateTokenString(Token token) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", token.getType());

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(token.getUsername())
                .issuedAt(token.getCreationDate())
                .expiration(token.getExpirationDate())
                .claims()
                .add(claims)
                .and()
                .signWith(key)
                .compact();
    }

    public Token parseTokenString(String tokenString) {
        Claims claims = extractClaims(tokenString);

        try {
            String issuer = claims.getIssuer();
            String username = claims.getSubject();
            Token.Type type = Token.Type.valueOf(claims.get("type", String.class));
            Date creationDate = claims.getIssuedAt();
            Date expirationDate = claims.getExpiration();

            if (issuer == null || username == null || creationDate == null || expirationDate == null) {
                throw new MalformedTokenException(new NullPointerException());
            }

            return new Token(issuer, username, type, creationDate, expirationDate);
        } catch (NullPointerException e) {
            throw new MalformedTokenException(e);
        }
    }

    public boolean isAccessTokenValid(Token token, UserDTO user) {
        return token.getType() == Token.Type.ACCESS && token.getIssuer().equals(ISSUER)
                && token.getExpirationDate().after(new Date())
                && token.getUsername().equals(user.getUsername())
                && user.getAccessToken().equals(generateTokenString(token));
    }

    public boolean isRefreshTokenValid(Token token, UserDTO user) {
        return token.getType() == Token.Type.REFRESH && token.getIssuer().equals(ISSUER)
                && token.getExpirationDate().after(new Date())
                && token.getUsername().equals(user.getUsername())
                && user.getRefreshToken().equals(generateTokenString(token));
    }

    private Claims extractClaims(String tokenString) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(tokenString)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new MalformedTokenException(e);
        }
    }
}
