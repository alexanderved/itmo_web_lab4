package web.exceptions;

public class UnauthorizedRequestException extends RuntimeException {
    public UnauthorizedRequestException() {
        super("Неавторизованный запрос к ресурсу");
    }
}
