package dk.kea.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.kea.library.model.Reservation;
import dk.kea.library.model.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByWorkIdAndStatus(String workId, ReservationStatus status);

    Optional<Reservation> findByWorkIdAndStatus(String workId, ReservationStatus status);

    long countByUserIdAndStatus(Long userId, ReservationStatus status);
}
