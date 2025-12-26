package web.db.postgresql;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import web.db.HibernateCfg;
import web.db.PointDB;
import web.db.UserDB;
import web.db.dto.HitPointDTO;
import web.db.dto.MissPointDTO;
import web.db.dto.PointDTO;
import web.db.dto.UserDTO;
import web.domain.Point;
import web.domain.Token;
import web.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton(name = "pointDB")
public class PostgreSQLPointDB implements PointDB {
    @EJB
    private HibernateCfg hibernateCfg;

    @EJB
    private UserDB userDB;

    @Override
    public int length(Token t) {
        try {
            UserDTO user = userDB.find(t.getUsername());

            return hibernateCfg.getSessionFactory().fromTransaction(session -> {
                String hql = "SELECT COUNT(p) FROM PointDTO p WHERE p.user.id = :id";
                Query<Long> query = session.createQuery(hql, Long.class);
                query.setParameter("id", user.getId());

                return query.uniqueResult();
            }).intValue();
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void storePoints(Point[] points, Token t) {
        try {
            UserDTO user = userDB.find(t.getUsername());

            hibernateCfg.getSessionFactory().fromTransaction(session -> {
                for (Point p : points) {
                    try {
                        if (p.isHit()) {
                            session.persist(new HitPointDTO(p, user));
                        } else {
                            session.persist(new MissPointDTO(p, user));
                        }
                    } catch (ConstraintViolationException ignored) {

                    }
                }

                return null;
            });
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Point> getPoints(Token t) {
        try {
            UserDTO user = userDB.find(t.getUsername());

            return hibernateCfg.getSessionFactory().fromTransaction(session -> {
                String hql = "SELECT p FROM PointDTO p ORDER BY p.id DESC";
                TypedQuery<PointDTO> query = session.createQuery(hql, PointDTO.class);

                return new ArrayList<>(query.getResultList()
                        .stream()
                        .map(PointDTO::toDomain)
                        .toList());
            });
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearPoints(Token t) {
        try {
            UserDTO user = userDB.find(t.getUsername());

            hibernateCfg.getSessionFactory().fromTransaction(session -> {
                String hql = "DELETE FROM PointDTO p WHERE p.user.id = :id";
                var query = session.createMutationQuery(hql);
                query.setParameter("id", user.getId());
                query.executeUpdate();

                return null;
            });
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
