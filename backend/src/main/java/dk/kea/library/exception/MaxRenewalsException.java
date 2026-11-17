package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class MaxRenewalsException extends DomainException {

    public MaxRenewalsException() {
        super("MAX_RENEWALS", HttpStatus.CONFLICT, "The loan has already been renewed 2 times");
    }
}
