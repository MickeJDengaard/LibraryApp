package unitTests;

import dk.kea.library.service.FeeCalculator;
import dk.kea.library.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FeeCalculatorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    //Positive
    @ParameterizedTest
    @CsvSource({
            "2025-11-13, 0",
            "2025-11-14, 0",
            "2025-11-15, 0",
            "2025-11-16, 0",
            "2025-11-17, 5",
            "2025-11-18, 10",
            "2025-12-13, 135",
            "2025-12-25, 195",
            "2025-12-26, 200",
            "2025-12-27, 200",
            "2025-12-28, 200",
            "2026-01-13, 200"

    })
    void feeForPositiveTest(LocalDate date, int expected) {
        FeeCalculator feeCalculator = new FeeCalculator(); //Arrange
        LocalDate due = LocalDate.parse("2025-11-13");

        int result = feeCalculator.feeFor(due, date);  //Act

        assertEquals(expected, result); //Assert

    }

}