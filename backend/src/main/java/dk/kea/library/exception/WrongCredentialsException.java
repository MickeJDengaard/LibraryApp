package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

public class WrongCredentialsException extends DomainException {

    public WrongCredentialsException() {
        super("WRONG_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Wrong username or password");
    }
}
