package web.domain;

import java.util.Date;

public class TokenPair {
    private final Token access;
    private final Token refresh;

    public TokenPair(String issuer, String username,
                     Date creationDate,
                     Date accessExpirationDate,
                     Date refreshExpirationDate) {
        access = new Token(issuer, username, Token.Type.ACCESS,
                creationDate, accessExpirationDate);
        refresh = new Token(issuer, username, Token.Type.REFRESH,
                creationDate, refreshExpirationDate);
    }

    public TokenPair(Token access, Token refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public Token getAccess() {
        return access;
    }

    public Token getRefresh() {
        return refresh;
    }
}
