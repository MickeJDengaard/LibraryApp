package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class LoanLimitReachedException extends DomainException {

    public LoanLimitReachedException() {
        super("LOAN_LIMIT_REACHED", HttpStatus.CONFLICT, "You already have 5 books on loan");
    }
}
