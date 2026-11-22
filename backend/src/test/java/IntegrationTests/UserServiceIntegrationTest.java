package IntegrationTests;

import dk.kea.library.LibraryApplication;
import dk.kea.library.model.User;
import dk.kea.library.service.LoanService;
import dk.kea.library.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes= LibraryApplication.class)
@Import(Postgresconfig.class)
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void signup() {
        User result = userService.signup("newuser", "password123");

        assertEquals("newuser", result.getUsername());
    }

    @Test
    void login() {
        User result = userService.login("newuser", "password123");

        assertEquals("newuser", result.getUsername());
    }

    @Test
    void loginWithWrongPasswordFails()  {
         assertThrows(Exception.class, ()-> userService.login("newuser", "wrongPassword"));
    }
}