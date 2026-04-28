package web.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import web.db.UserDB;
import web.db.dto.UserDTO;
import web.domain.Token;
import web.exceptions.UserNotFoundException;
import web.service.TokenService;

import java.util.Set;

@ApplicationScoped
public class JWTAuthMechanism implements HttpAuthenticationMechanism {
    @EJB
    private TokenService tokenService;

    @EJB
    private UserDB userDB;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext context)
            throws AuthenticationException {
        System.out.println("--------- START AUTH");

        if (!context.isProtected()) {
            return context.doNothing();
        }

        if (request.getMethod().equals("OPTIONS")) {
            return context.doNothing();
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println(authorizationHeader);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            addCORS(response);
            return context.responseUnauthorized();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        UserDTO user = null;
        try {
            user = userDB.find(token.getUsername());
        } catch (UserNotFoundException e) {
            addCORS(response);
            return context.responseUnauthorized();
        }

        if (!tokenService.isAccessTokenValid(token, user)) {
            addCORS(response);
            return context.responseUnauthorized();
        }

        return context.notifyContainerAboutLogin(user.getUsername(), Set.of("user"));
    }

    private static void addCORS(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Max-Age", "1209600");
    }
}
