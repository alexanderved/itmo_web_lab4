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
            System.out.println("--------- NOT PROTECTED");

            return context.doNothing();
        }

        if (request.getMethod().equals("OPTIONS")) {
            System.out.println("--------- OPTIONS");

            return context.doNothing();
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println(authorizationHeader);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            System.out.println("------------ NO HEADER");

            addCORS(response);
            return context.responseUnauthorized();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        UserDTO user = null;
        try {
            user = userDB.find(token.getUsername());
        } catch (UserNotFoundException e) {
            System.out.println("------------ USER NOT FOUND");

            addCORS(response);
            return context.responseUnauthorized();
        }

        if (!tokenService.isAccessTokenValid(token, user)) {
            System.out.println("------------ TOKEN EXPIRED");

            addCORS(response);
            return context.responseUnauthorized();
        }

        System.out.println("--------- AUTH");

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
