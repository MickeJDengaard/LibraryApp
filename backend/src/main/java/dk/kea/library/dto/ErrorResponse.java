package dk.kea.library.dto;

/** The one JSON shape every error uses: {"code": "...", "message": "..."} */
public record ErrorResponse(String code, String message) {
}
