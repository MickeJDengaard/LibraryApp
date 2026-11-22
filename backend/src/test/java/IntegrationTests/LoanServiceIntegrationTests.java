package IntegrationTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(Postgresconfig.class)
class LoanServiceIntegrationTests {


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void borrow() {


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