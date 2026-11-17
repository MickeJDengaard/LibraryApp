package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class InvalidUsernameException extends DomainException {

    public InvalidUsernameException() {
        super("INVALID_USERNAME", HttpStatus.BAD_REQUEST, "Username must be 3-20 characters: letters, digits or _");
    }
}
