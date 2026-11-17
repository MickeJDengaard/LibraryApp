package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class NotLoggedInException extends DomainException {

    public NotLoggedInException() {
        super("NOT_LOGGED_IN", HttpStatus.UNAUTHORIZED, "You must be logged in");
    }
}
