package IntegrationTests;

import dk.kea.library.client.OpenLibraryClient;
import dk.kea.library.model.Loan;
import dk.kea.library.model.User;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.repository.ReservationRepository;
import dk.kea.library.repository.UserRepository;
import dk.kea.library.service.LoanService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Import(Postgresconfig.class)
class LoanServiceIntegrationTests {
    private LoanService loanService;
    private LoanRepository loans;
    private ReservationRepository reservations;
    private UserRepository users;
    private OpenLibraryClient openLibrary;
    private dk.kea.library.service.FeeCalculator feeCalculator;
    private Clock clock;


    @BeforeEach
    void setUp() {
        loanService = new LoanService(loans, reservations, users, openLibrary, feeCalculator, clock);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void borrow() {
        Loan result = loanService.borrow(1, "1");

        assertEquals("1", result.getWorkId());
       // assertTrue(loans.findById(result.getId()));
    }

    @Test
    void renew() {
    }

    @Test
    void returnLoan() {
    }

    @Test
    void myLoans() {
    }
}