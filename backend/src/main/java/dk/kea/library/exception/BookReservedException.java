package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class BookReservedException extends DomainException {

    public BookReservedException() {
        super("BOOK_RESERVED", HttpStatus.CONFLICT, "The book has an active reservation by another user");
    }
}
