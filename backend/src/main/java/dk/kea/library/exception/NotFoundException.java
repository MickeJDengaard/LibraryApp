package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends DomainException {

    public NotFoundException() {
        super("NOT_FOUND", HttpStatus.NOT_FOUND, "Not found");
    }
}
