package web.domain;

import java.util.Date;
import java.util.Objects;

public class Token {
    private final String issuer;
    private final String username;
    private final Type type;
    private final Date creationDate;
    private final Date expirationDate;

    public Token(String issuer, String username, Type type,
                 Date creationDate, Date expirationDate) {
        this.issuer = issuer;
        this.username = username;
        this.type = type;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getUsername() {
        return username;
    }

    public Type getType() {
        return type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public enum Type {
        ACCESS,
        REFRESH
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Token token = (Token) o;
        return Objects.equals(issuer, token.issuer)
                && Objects.equals(username, token.username)
                && type == token.type
                && Objects.equals(creationDate, token.creationDate)
                && Objects.equals(expirationDate, token.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuer, username, type, creationDate, expirationDate);
    }
}
