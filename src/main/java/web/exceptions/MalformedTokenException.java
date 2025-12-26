package web.exceptions;

public class MalformedTokenException extends RuntimeException {
    public MalformedTokenException(Throwable e) {
        super(e);
    }
}
