package web.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import web.db.PointDB;
import web.domain.Point;
import web.domain.Token;

import java.util.List;

@Stateful(name = "pointService")
public class PointService {
    @EJB
    private PointDB pointDB;

    public void storePoints(Point[] p, Token t) {
        pointDB.storePoints(p, t);
    }

    public List<Point> getPoints(Token t) {
        return pointDB.getPoints(t);
    }

    public void clearPoints(Token t) {
        pointDB.clearPoints(t);
    }
}
