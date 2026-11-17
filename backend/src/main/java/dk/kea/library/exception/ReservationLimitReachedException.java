package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class ReservationLimitReachedException extends DomainException {

    public ReservationLimitReachedException() {
        super("RESERVATION_LIMIT_REACHED", HttpStatus.CONFLICT, "You already have 3 active reservations");
    }
}
