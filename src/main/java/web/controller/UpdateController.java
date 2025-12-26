package web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import web.domain.Token;
import web.exceptions.UnauthorizedRequestException;
import web.service.TokenService;
import web.service.UpdateService;

@Path("update/")
public class UpdateController {
    @EJB
    UpdateService updateService;

    @EJB
    TokenService tokenService;

    @Path("/wait")
    @GET
    public void waitUpdate(@Context HttpHeaders headers,
                           @Suspended AsyncResponse res) {
        System.out.println("---------- WAIT");

        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedRequestException();
        }

        System.out.println("---------- WAIT AUTH");

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);
        String username = token.getUsername();

        updateService.waitUpdate(username, res);
    }

    @Path("/notify")
    @POST
    public void notifyUpdate(@Context HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedRequestException();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);
        String username = token.getUsername();

        updateService.notifyUpdate(username);
    }
}
