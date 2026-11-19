package unitTests;

import dk.kea.library.model.Loan;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.service.FeeCalculator;
import dk.kea.library.service.LoanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class LoanServiceTest {

    private MockedStatic<LocalDate> mockedStatic;
    private Loan loan = new Loan();
    private Loan loan2 = new Loan();
    @Mock
    private LoanRepository loanRepository;

    private FeeCalculator feeCalculator;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        if(mockedStatic != null) {
            mockedStatic.close();
        }
    }


    @ParameterizedTest
    @CsvSource({
            "2025-11-13, 2025-11-11, 50",
            "2025-11-13, 2025-11-13, 40",
            "2025-11-18, 2025-11-20, 30",
            "2025-11-18, 2025-11-13, 35",
            "2026-02-02, 2024-11-13, 230"

    })
    void owedKr(LocalDate dueDate1, LocalDate dueDate2, int expected) {
        //Arrange
        List<Loan> returnedLoans = new ArrayList<>();
        List<Loan> unreturnedLoans = new ArrayList<>();

        feeCalculator = new FeeCalculator();

        //Create loans
        loan.setDueOn(dueDate1);
        loan.setFeeKr(20);

        loan2.setDueOn(dueDate2);
        loan2.setFeeKr(10);

        //Add loans to lists
        returnedLoans.add(loan);
        returnedLoans.add(loan2);

        unreturnedLoans.add(loan);
        unreturnedLoans.add(loan2);

        //Mock loanRepository
        loanRepository = mock(LoanRepository.class);
            when(loanRepository.findByUserIdAndReturnedOnIsNotNullAndFeePaidFalse(1L)).thenReturn(returnedLoans);
            when(loanRepository.findByUserIdAndReturnedOnIsNull(1L)).thenReturn(unreturnedLoans);


        //Mock LocalDate.now() to 2025-11-17
        Clock clock = Clock.fixed(Instant.parse("2025-11-17T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDate date = LocalDate.now(clock);
        mockedStatic = mockStatic(LocalDate.class, CALLS_REAL_METHODS);
        when(LocalDate.now()).thenReturn(date);

        //Create loanService
        LoanService loanService = new LoanService(loanRepository, feeCalculator, clock);

        //Act
        int result = loanService.owedKr(1L);

        //Assert
        assertEquals(expected, result);

    }
}