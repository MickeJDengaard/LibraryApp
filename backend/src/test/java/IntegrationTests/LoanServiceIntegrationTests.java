package IntegrationTests;

import dk.kea.library.LibraryApplication;
import dk.kea.library.client.OpenLibraryClient;
import dk.kea.library.exception.AlreadyReturnedException;
import dk.kea.library.exception.BookReservedException;
import dk.kea.library.exception.LoanOverdueException;
import dk.kea.library.exception.MaxRenewalsException;
import dk.kea.library.exception.NotYourLoanException;
import dk.kea.library.model.Loan;
import dk.kea.library.model.User;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.repository.ReservationRepository;
import dk.kea.library.repository.UserRepository;
import dk.kea.library.service.LoanService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes= LibraryApplication.class)
@Import(Postgresconfig.class)
class LoanServiceIntegrationTests {
    @Autowired
    private LoanService loanService;
    @Autowired
    private LoanRepository loans;
    @Autowired
    private ReservationRepository reservations;
    @Autowired
    private UserRepository users;
    private OpenLibraryClient openLibrary;
    private dk.kea.library.service.FeeCalculator feeCalculator;
    private Clock clock;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void borrow() {
        // Act
        Loan result = loanService.borrow(1L, "OL3372608W");
        Loan active = loans.findByWorkIdAndReturnedOnIsNull("OL3372608W").orElseThrow();

        // Assert
        assertEquals("OL3372608W", result.getWorkId());
        assertTrue(loans.findById(result.getId()).isPresent());
        assertTrue(loans.existsByWorkIdAndReturnedOnIsNull("OL3372608W"));
        assertEquals(1L, active.getUser().getId());
        assertNull(active.getReturnedOn());
    }

    @Test
    void renew() {
        // Arrange
        LocalDate oldDue = loans.findById(1L).orElseThrow().getDueOn();
        int oldCount = loans.findById(1L).orElseThrow().getRenewCount();

        // Act
        Loan result = loanService.renew(1L, 1L);

        // Assert
        assertEquals(oldDue.plusDays(14), result.getDueOn());
        assertEquals(oldCount + 1, result.getRenewCount());
    }

    @Test
    void returnLoan() {
        Loan result = loanService.returnLoan(1L, 1L);

        assertEquals(LocalDate.now(), result.getReturnedOn());
        assertEquals(0, result.getFeeKr());
        assertFalse(result.isActive());
    }

    @Test
    void myLoans() {
        List<Loan> result = loanService.myLoans(1L);

        assertEquals(3, result.size());
    }
}