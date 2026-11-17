package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super("INVALID_PASSWORD", HttpStatus.BAD_REQUEST, "Password must be 8-64 characters");
    }
}
