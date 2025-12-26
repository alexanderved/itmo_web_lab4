package web.exceptions;

public class UserNameAlreadyTakenException extends Exception {
    private final String name;

    public UserNameAlreadyTakenException(String name) {
        super("Имя пользователя '" + name + "' занято");

        this.name = name;
    }

    public String getName() {
        return name;
    }
}

