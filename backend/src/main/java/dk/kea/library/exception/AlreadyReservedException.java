package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class AlreadyReservedException extends DomainException {

    public AlreadyReservedException() {
        super("ALREADY_RESERVED", HttpStatus.CONFLICT, "The book is already reserved");
    }
}
