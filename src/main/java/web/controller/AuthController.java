package web.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import web.domain.TokenPair;
import web.domain.TokenInfo;
import web.domain.UserInfo;
import web.exceptions.InvalidRefreshTokenException;
import web.exceptions.UserNameAlreadyTakenException;
import web.exceptions.UserNotFoundException;
import web.service.AuthService;
import web.service.TokenService;

@Path("auth/")
public class AuthController {
    @EJB
    private TokenService tokenService;

    @EJB
    private AuthService authService;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @PermitAll
    @Path("registration/")
    public void registration(UserInfo userInfo) {
        try {
            authService.register(userInfo.getUsername(), userInfo.getPassword());
        } catch (UserNameAlreadyTakenException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @PermitAll
    @Path("login/")
    public TokenInfo login(UserInfo userInfo) {
        try {
            TokenPair pair = authService.login(
                    userInfo.getUsername(), userInfo.getPassword());
            String access = tokenService.generateTokenString(pair.getAccess());
            String refresh = tokenService.generateTokenString(pair.getRefresh());

            return new TokenInfo(access, refresh);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @PermitAll
    @Path("refresh/")
    public TokenInfo refresh(TokenInfo tokenInfo) {
        try {
            TokenPair pair = authService.refresh(
                    tokenService.parseTokenString(tokenInfo.getRefresh()));
            String access = tokenService.generateTokenString(pair.getAccess());
            String refresh = tokenService.generateTokenString(pair.getRefresh());

            return new TokenInfo(access, refresh);
        } catch (InvalidRefreshTokenException e) {
            throw new RuntimeException(e);
        }
    }
}
