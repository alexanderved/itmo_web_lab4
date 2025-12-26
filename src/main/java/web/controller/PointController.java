package web.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import web.domain.Point;
import web.domain.Token;
import web.domain.UncheckedPoint;
import web.exceptions.UnauthorizedRequestException;
import web.model.PointChecker;
import web.service.PointService;
import web.service.TokenService;

import java.util.*;
import java.util.stream.Collectors;

@Path("points/")
public class PointController {
    @EJB
    private PointService pointService;

    @EJB
    private TokenService tokenService;

    @POST
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("store-points/")
    public Point[] storePoints(@Context HttpHeaders headers, UncheckedPoint[] upoints) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedRequestException();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        List<Point> pointList = Arrays.stream(upoints)
                .map(PointChecker::checkPoint)
                .collect(Collectors.toCollection(ArrayList::new));
        pointService.storePoints(pointList.toArray(Point[]::new), token);
        Collections.reverse(pointList);

        return pointList.toArray(Point[]::new);
    }

    @GET
    @PermitAll
    @Produces({MediaType.APPLICATION_JSON})
    @Path("get-points/")
    public List<Point> getPoints(@Context HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedRequestException();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        return pointService.getPoints(token);
    }

    @DELETE
    @RolesAllowed({"user"})
    @Path("clear-points/")
    public void clearPoints(@Context HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedRequestException();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        pointService.clearPoints(token);
    }
}
