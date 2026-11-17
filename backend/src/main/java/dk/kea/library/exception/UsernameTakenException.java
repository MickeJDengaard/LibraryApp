package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class UsernameTakenException extends DomainException {

    public UsernameTakenException() {
        super("USERNAME_TAKEN", HttpStatus.CONFLICT, "Username is already taken");
    }
}
