package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class AlreadyReturnedException extends DomainException {

    public AlreadyReturnedException() {
        super("ALREADY_RETURNED", HttpStatus.CONFLICT, "The loan has already been returned");
    }
}
