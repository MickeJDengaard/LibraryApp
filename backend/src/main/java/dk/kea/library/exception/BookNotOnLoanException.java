package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class BookNotOnLoanException extends DomainException {

    public BookNotOnLoanException() {
        super("BOOK_NOT_ON_LOAN", HttpStatus.CONFLICT, "You can only reserve a book that someone else has on loan");
    }
}
