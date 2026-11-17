package dk.kea.library.dto;

import java.util.List;

/**
 * A book with the library's own flags.
 * available = nobody has it on loan, reserved = it has an active reservation.
 */
public record BookResponse(String workId,
                           String title,
                           List<String> authors,
                           Integer firstPublishYear,
                           boolean available,
                           boolean reserved) {
}
