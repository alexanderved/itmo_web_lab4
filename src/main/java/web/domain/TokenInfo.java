package web.domain;

import java.io.Serializable;

public class TokenInfo implements Serializable {
    private String access;
    private String refresh;

    public TokenInfo() {

    }

    public TokenInfo(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}
