package IntegrationTests;

import dk.kea.library.LibraryApplication;
import dk.kea.library.model.Reservation;
import dk.kea.library.model.ReservationStatus;
import dk.kea.library.repository.ReservationRepository;
import dk.kea.library.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import dk.kea.library.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static dk.kea.library.model.ReservationStatus.CANCELLED;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes= LibraryApplication.class)
@Import(Postgresconfig.class)
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    //private ReservationStatus reservationStatus;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void reserve() {
        Reservation result = reservationService.reserve(1L, "OL82563W");

        assertEquals("OL82563W", result.getWorkId());
        assertEquals(LocalDate.now() ,result.getCreatedAt().toLocalDate());
    }

    @Test
    void cancel() {
        reservationService.cancel(1L, 1L);
        Reservation reservation = reservationRepository.findById(1L).orElseThrow();

        assertEquals(CANCELLED, reservation.getStatus());
    }

    @Test
    void myReservations() {
        List<Reservation> reservations = reservationService.myReservations(8L);

        assertEquals(2, reservations.size());
    }
}