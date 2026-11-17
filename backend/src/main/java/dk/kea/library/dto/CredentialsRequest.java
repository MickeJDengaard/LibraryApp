package dk.kea.library.dto;

/** Body of POST /api/auth/signup and POST /api/auth/login */
public record CredentialsRequest(String username, String password) {
}
