package dk.kea.library.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.kea.library.exception.AlreadyReservedException;
import dk.kea.library.exception.BookNotOnLoanException;
import dk.kea.library.exception.NotFoundException;
import dk.kea.library.exception.NotYourReservationException;
import dk.kea.library.exception.ReservationLimitReachedException;
import dk.kea.library.model.Loan;
import dk.kea.library.model.Reservation;
import dk.kea.library.model.ReservationStatus;
import dk.kea.library.model.User;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.repository.ReservationRepository;
import dk.kea.library.repository.UserRepository;

@Service
public class ReservationService {

    public static final int MAX_ACTIVE_RESERVATIONS = 3;

    private final ReservationRepository reservations;
    private final LoanRepository loans;
    private final UserRepository users;
    private final Clock clock;

    public ReservationService(ReservationRepository reservations, LoanRepository loans,
                              UserRepository users, Clock clock) {
        this.reservations = reservations;
        this.loans = loans;
        this.users = users;
        this.clock = clock;
    }

    @Transactional
    public Reservation reserve(long userId, String workId) {
        User user = users.findById(userId).orElseThrow(NotFoundException::new);

        Optional<Loan> activeLoan = loans.findByWorkIdAndReturnedOnIsNull(workId);
        if (activeLoan.isEmpty() || activeLoan.get().getUser().getId().equals(userId)) {
            throw new BookNotOnLoanException(); // BR-10: someone else must have it on loan
        }
        if (reservations.existsByWorkIdAndStatus(workId, ReservationStatus.ACTIVE)) {
            throw new AlreadyReservedException(); // BR-10: max 1 active reservation per book
        }
        if (reservations.countByUserIdAndStatus(userId, ReservationStatus.ACTIVE) >= MAX_ACTIVE_RESERVATIONS) {
            throw new ReservationLimitReachedException(); // BR-10: max 3 per user
        }

        Reservation reservation = new Reservation(user, workId, LocalDateTime.now(clock));
        return reservations.save(reservation);
    }

    @Transactional
    public void cancel(long reservationId, long userId) {
        Reservation reservation = reservations.findById(reservationId).orElseThrow(NotFoundException::new);
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new NotFoundException();
        }
        if (!reservation.getUser().getId().equals(userId)) {
            throw new NotYourReservationException();
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservations.save(reservation);
    }

    public List<Reservation> myReservations(long userId) {
        return reservations.findByUserIdAndStatusOrderByCreatedAtDesc(userId, ReservationStatus.ACTIVE);
    }
}
