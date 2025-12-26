package web.db.postgresql;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import web.db.HibernateCfg;
import web.db.UserDB;
import web.db.dto.UserDTO;
import web.exceptions.UserNameAlreadyTakenException;
import web.exceptions.UserNotFoundException;
import web.utils.Hasher;

import java.util.List;

@Singleton(name = "userDB")
public class PostgreSQLUserDB implements UserDB {
    @EJB
    private HibernateCfg hibernateCfg;

    @EJB
    private Hasher hasher;

    @Override
    public UserDTO find(String name) throws UserNotFoundException {
        UserDTO user = hibernateCfg.getSessionFactory().fromTransaction(session -> {
            return findDTOByName(session, name);
        });

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public void add(String username, String passwordHash, String salt)
            throws UserNameAlreadyTakenException {
        boolean isAdded = hibernateCfg.getSessionFactory().fromTransaction(session -> {
            try {
                session.persist(new UserDTO(username,
                        passwordHash,
                        salt));
            } catch (ConstraintViolationException e) {
                return false;
            }

            return true;
        });

        if (!isAdded) {
            throw new UserNameAlreadyTakenException(username);
        }
    }

    @Override
    public void update(UserDTO user) throws UserNotFoundException {
        boolean isUpdated = hibernateCfg.getSessionFactory().fromTransaction(session -> {
            if (user == null) {
                return false;
            }

            try {
                session.merge(user);
            } catch (ConstraintViolationException e) {
                return false;
            }

            return true;
        });

        if (!isUpdated) {
            throw new UserNotFoundException();
        }
    }

    private UserDTO findDTOByName(Session session, String name) {
        var builder = session.getCriteriaBuilder();
        var query = builder.createQuery(UserDTO.class);
        var root = query.from(UserDTO.class);
        query.select(root)
                .where(builder.equal(root.get("username"), name));

        List<UserDTO> users = session.createQuery(query).getResultList();
        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}
