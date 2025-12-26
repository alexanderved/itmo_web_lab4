package web.exceptions;

public class InvalidRefreshTokenException extends Exception {
    public InvalidRefreshTokenException() {

    }

    public InvalidRefreshTokenException(Throwable e) {
        super(e.toString(), e);
    }
}
