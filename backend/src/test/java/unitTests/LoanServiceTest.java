package unitTests;

import dk.kea.library.model.Loan;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.service.LoanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    private ArrayList<Loan> loanList;
    private MockedStatic<LocalDate> mockedStatic;
    @Mock
    private Loan loan = new Loan();
    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        loanList = new ArrayList<>();
        loanList.add(new Loan());
        loanList.add(new Loan());
    }

    @AfterEach
    void tearDown() {
        if(mockedStatic != null) {
            mockedStatic.close();
        }
    }


    @Test
    void owedKr() {
        //Arrange
        LoanService loanService = new LoanService();

        loan = mock(Loan.class);
            when(loan.getFeeKr()).thenReturn(5);
            when(loan.getDueOn()).thenReturn(LocalDate.parse("2025-11-13"));


        loanRepository = mock(LoanRepository.class);
            when(loanRepository.findByUserIdAndReturnedOnIsNotNullAndFeePaidFalse(1L)).thenReturn(loanList);

        Clock clock = Clock.fixed(Instant.parse("2025-11-17T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDate date = LocalDate.now(clock);
        mockedStatic = mockStatic(LocalDate.class);
        when(LocalDate.now()).thenReturn(date);

        //Act
        int result = loanService.owedKr(1L);

        //Assert
        assertEquals(10, result);

    }
}