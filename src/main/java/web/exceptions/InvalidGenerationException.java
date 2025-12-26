package web.exceptions;

public class InvalidGenerationException extends RuntimeException {
    public InvalidGenerationException() {
        super("Неверное поколение токена");
    }
}
