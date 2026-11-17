package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class NotYourReservationException extends DomainException {

    public NotYourReservationException() {
        super("NOT_YOUR_RESERVATION", HttpStatus.FORBIDDEN, "This reservation belongs to another user");
    }
}
