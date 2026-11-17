package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class BookNotAvailableException extends DomainException {

    public BookNotAvailableException() {
        super("BOOK_NOT_AVAILABLE", HttpStatus.CONFLICT, "The book is already on loan");
    }
}
