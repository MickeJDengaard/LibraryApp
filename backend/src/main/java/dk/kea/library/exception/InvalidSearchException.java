package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class InvalidSearchException extends DomainException {

    public InvalidSearchException() {
        super("INVALID_SEARCH", HttpStatus.BAD_REQUEST, "Search text must be 2-100 characters");
    }
}
