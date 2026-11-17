package dk.kea.library.dto;

import java.time.LocalDateTime;

import dk.kea.library.model.Reservation;
import dk.kea.library.model.ReservationStatus;

public record ReservationResponse(Long id, String workId, LocalDateTime createdAt, ReservationStatus status) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getWorkId(),
                reservation.getCreatedAt(), reservation.getStatus());
    }
}
