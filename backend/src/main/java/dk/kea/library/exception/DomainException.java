package dk.kea.library.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for all business-rule errors. Each subclass carries the code
 * string the API returns (e.g. LOAN_OVERDUE) and the HTTP status to use.
 */
public abstract class DomainException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    protected DomainException(String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
