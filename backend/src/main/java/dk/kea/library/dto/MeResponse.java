package dk.kea.library.dto;

/** Response of GET /api/auth/me */
public record MeResponse(Long id, String username, int owedKr) {
}
