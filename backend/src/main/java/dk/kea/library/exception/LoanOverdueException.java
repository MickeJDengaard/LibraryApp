package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class LoanOverdueException extends DomainException {

    public LoanOverdueException() {
        super("LOAN_OVERDUE", HttpStatus.CONFLICT, "An overdue loan cannot be renewed");
    }
}
