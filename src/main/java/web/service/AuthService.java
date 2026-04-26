package web.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import web.db.UserDB;
import web.db.dto.UserDTO;
import web.domain.Token;
import web.domain.TokenPair;
import web.exceptions.InvalidRefreshTokenException;
import web.exceptions.UserNameAlreadyTakenException;
import web.exceptions.UserNotFoundException;
import web.utils.Hasher;
import web.utils.SaltGenerator;

@Stateful(name = "authService")
public class AuthService {
    @EJB
    private Hasher hasher;

    @EJB
    private UserDB userDB;

    @EJB
    private TokenService tokenService;

    public void register(String username, String password)
            throws UserNameAlreadyTakenException {
        String salt = SaltGenerator.generateSalt();
        userDB.add(username, hasher.hash(password, salt), salt);
    }

    public TokenPair login(String username, String password)
            throws UserNotFoundException {
        UserDTO user = userDB.find(username);
        String hashedPassword = hasher.hash(password, user.getSalt());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            throw new UserNotFoundException();
        }

        return updateUserTokens(user);
    }

    public TokenPair refresh(Token token)
            throws InvalidRefreshTokenException {
        try {
            UserDTO user = userDB.find(token.getUsername());
            if (!tokenService.isRefreshTokenValid(token, user)) {
                throw new InvalidRefreshTokenException();
            }

            return updateUserTokens(user);
        } catch (UserNotFoundException e) {
            throw new InvalidRefreshTokenException(e);
        }
    }

    private TokenPair updateUserTokens(UserDTO user) throws UserNotFoundException {
        TokenPair pair = tokenService.generateTokenPair(user);

        user.setAccessToken(tokenService.generateTokenString(pair.getAccess()));
        user.setRefreshToken(tokenService.generateTokenString(pair.getRefresh()));

        userDB.update(user);

        return pair;
    }
}
