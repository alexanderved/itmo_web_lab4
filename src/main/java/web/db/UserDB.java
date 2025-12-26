package web.db;

import jakarta.ejb.Local;
import web.db.dto.UserDTO;
import web.exceptions.UserNameAlreadyTakenException;
import web.exceptions.UserNotFoundException;

@Local
public interface UserDB {
    UserDTO find(String name) throws UserNotFoundException;
    void add(String username, String passwordHash, String salt)
            throws UserNameAlreadyTakenException;
    void update(UserDTO user) throws UserNotFoundException;
}
