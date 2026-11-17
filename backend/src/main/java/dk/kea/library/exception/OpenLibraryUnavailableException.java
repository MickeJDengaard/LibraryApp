package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class OpenLibraryUnavailableException extends DomainException {

    public OpenLibraryUnavailableException() {
        super("OPEN_LIBRARY_UNAVAILABLE", HttpStatus.BAD_GATEWAY, "Open Library is not available right now");
    }
}
