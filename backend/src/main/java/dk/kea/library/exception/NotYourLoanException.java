package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class NotYourLoanException extends DomainException {

    public NotYourLoanException() {
        super("NOT_YOUR_LOAN", HttpStatus.FORBIDDEN, "This loan belongs to another user");
    }
}
