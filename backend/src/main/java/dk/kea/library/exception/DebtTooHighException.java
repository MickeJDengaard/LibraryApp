package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class DebtTooHighException extends DomainException {

    public DebtTooHighException() {
        super("DEBT_TOO_HIGH", HttpStatus.PAYMENT_REQUIRED, "You owe 100 kr or more and cannot borrow");
    }
}
