package web.db;

import web.domain.Point;
import web.domain.Token;

import java.util.List;

public interface PointDB {
    int length(Token t);
    void storePoints(Point[] p, Token t);
    List<Point> getPoints(Token t);
    void clearPoints(Token t);
}
