package dk.kea.library.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.kea.library.client.OpenLibraryBook;
import dk.kea.library.client.OpenLibraryClient;
import dk.kea.library.exception.AlreadyReturnedException;
import dk.kea.library.exception.BookNotAvailableException;
import dk.kea.library.exception.BookReservedException;
import dk.kea.library.exception.DebtTooHighException;
import dk.kea.library.exception.LoanLimitReachedException;
import dk.kea.library.exception.LoanOverdueException;
import dk.kea.library.exception.MaxRenewalsException;
import dk.kea.library.exception.NotFoundException;
import dk.kea.library.exception.NotYourLoanException;
import dk.kea.library.model.Loan;
import dk.kea.library.model.Reservation;
import dk.kea.library.model.ReservationStatus;
import dk.kea.library.model.User;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.repository.ReservationRepository;
import dk.kea.library.repository.UserRepository;

@Service
public class LoanService {

    public static final int MAX_ACTIVE_LOANS = 5;
    public static final int LOAN_DAYS = 14;
    public static final int MAX_RENEWALS = 2;
    public static final int DEBT_LIMIT_KR = 100;

    private LoanRepository loans;
    private ReservationRepository reservations;
    private UserRepository users;
    private OpenLibraryClient openLibrary;
    private dk.kea.library.service.FeeCalculator feeCalculator;
    private Clock clock;

    public LoanService(LoanRepository loans, ReservationRepository reservations, UserRepository users,
                       OpenLibraryClient openLibrary, dk.kea.library.service.FeeCalculator feeCalculator, Clock clock) {
        this.loans = loans;
        this.reservations = reservations;
        this.users = users;
        this.openLibrary = openLibrary;
        this.feeCalculator = feeCalculator;
        this.clock = clock;
    }

    public LoanService(LoanRepository loans, FeeCalculator feeCalculator, Clock clock) {
        this.loans = loans;
        this.feeCalculator = feeCalculator;
        this.clock = clock;
    }


    @Transactional
    public Loan borrow(long userId, String workId) {
        User user = users.findById(userId).orElseThrow(NotFoundException::new);

        if (loans.countByUserIdAndReturnedOnIsNull(userId) >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitReachedException(); // BR-03 → 409
        }
        if (owedKr(userId) >= DEBT_LIMIT_KR) {
            throw new DebtTooHighException(); // BR-06 → 402
        }
        if (loans.existsByWorkIdAndReturnedOnIsNull(workId)) {
            throw new BookNotAvailableException(); // BR-05 → 409
        }
        Optional<Reservation> reservation = reservations.findByWorkIdAndStatus(workId, ReservationStatus.ACTIVE);
        if (reservation.isPresent()) {
            if (!reservation.get().getUser().getId().equals(userId)) {
                throw new BookReservedException(); // BR-05 → 409
            }
            reservation.get().setStatus(ReservationStatus.FULFILLED); // BR-05: own reservation is fulfilled
            reservations.save(reservation.get());
        }

        OpenLibraryBook book = openLibrary.findWork(workId).orElseThrow(NotFoundException::new);

        LocalDate today = LocalDate.now(clock);
        Loan loan = new Loan(user, workId, book.title(), today, today.plusDays(LOAN_DAYS)); // BR-04
        return loans.save(loan);
    }

    @Transactional
    public Loan renew(long loanId, long userId) {
        Loan loan = loans.findById(loanId).orElseThrow(NotFoundException::new);
        if (loan.getUser().getId() != userId)    throw new NotYourLoanException();      // BR-09 → 403
        if (loan.getReturnedOn() != null)        throw new AlreadyReturnedException();  // → 409
        LocalDate today = LocalDate.now(clock);
        if (today.isAfter(loan.getDueOn()))      throw new LoanOverdueException();      // BR-07 → 409
        if (loan.getRenewCount() >= MAX_RENEWALS) throw new MaxRenewalsException();     // BR-07 → 409
        if (reservations.existsByWorkIdAndStatus(loan.getWorkId(), ReservationStatus.ACTIVE))
                                                 throw new BookReservedException();     // BR-07 → 409
        loan.setDueOn(loan.getDueOn().plusDays(LOAN_DAYS));
        loan.setRenewCount(loan.getRenewCount() + 1);
        return loans.save(loan);
    }

    @Transactional
    public Loan returnLoan(long loanId, long userId) {
        Loan loan = loans.findById(loanId).orElseThrow(NotFoundException::new);
        if (loan.getUser().getId() != userId) {
            throw new NotYourLoanException(); // BR-09 → 403
        }
        if (loan.getReturnedOn() != null) {
            throw new AlreadyReturnedException(); // → 409
        }
        LocalDate today = LocalDate.now(clock);
        loan.setReturnedOn(today);
        loan.setFeeKr(feeCalculator.feeFor(loan.getDueOn(), today)); // BR-09: store the final fee
        return loans.save(loan);
    }

    public List<Loan> myLoans(long userId) {
        return loans.findByUserIdOrderByBorrowedOnDesc(userId);
    }

    /**
     * BR-06: what the user owes = unpaid fees on returned loans
     * + fees accrued so far on active loans (0 when not late).
     */
    public int owedKr(long userId) {
        int owed = 0;
        for (Loan loan : loans.findByUserIdAndReturnedOnIsNotNullAndFeePaidFalse(userId)) {
            owed += loan.getFeeKr();
        }
        LocalDate today = LocalDate.now(clock);
        for (Loan loan : loans.findByUserIdAndReturnedOnIsNull(userId)) {
            owed += feeCalculator.feeFor(loan.getDueOn(), today);
        }
        return owed;
    }

    /** Fee so far: the final fee when returned, else what it would be today. */
    public int feeSoFar(Loan loan) {
        if (loan.getReturnedOn() != null) {
            return loan.getFeeKr();
        }
        return feeCalculator.feeFor(loan.getDueOn(), LocalDate.now(clock));
    }
}
