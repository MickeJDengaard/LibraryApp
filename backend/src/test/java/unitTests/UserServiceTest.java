package unitTests;

import dk.kea.library.service.UserService;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    //Positive
    @ParameterizedTest
    @CsvSource({
            "val",
            "vali",
            "valid_username1",
            "validusername_12345",
            "validusername_123456"

    })
    void isValidUsernamePositiveTest(String username) {
        UserService userService = new UserService(); //Arrange

        boolean result = userService.isValidUsername(username);  //Act

        assertTrue(result); //Assert

    }

    //Negative
    @ParameterizedTest
    @CsvSource(value = {
            "''", //Empty string
            "i",
            "in",
            "invalidusername_12345",
            "invalidusername_123456",
            "invalidusernamelength12345678",
            "// -)(",
            "null"
    }, nullValues = {"null"})
    void isValidUsernameNegativeTest(String username) {
        UserService userService = new UserService(); //Arrange

        boolean result = userService.isValidUsername(username);  //Act

        assertFalse(result); //Assert

    }

    //Positive
    @ParameterizedTest
    @CsvSource({
            "validpas",
            "validpass",
            "valid-password1",
            "Valid_password_due_to_length_this_password_is_63_characters_lon",
            "Valid_password_due_to_length_this_password_is_64_characters_long"

    })
    void isValidPasswordPositiveTest(String password) {
        UserService userService = new UserService(); //Arrange

        boolean result = userService.isValidPassword(password);  //Act

        assertTrue(result); //Assert

    }

    //Negative
    @ParameterizedTest
    @CsvSource(value = {
            "''", //Empty string
            "i",
            "in",
            "inv",
            "invali",
            "invalid",
            "Invalid_password_due_to_length_this_password_is_65_characters_lon",
            "Invalid_password_due_to_length_this_password_is_66_characters_long",
            "null"
    }, nullValues = {"null"})
    void isValidPasswordNegativeTest(String password) {
        UserService userService = new UserService(); //Arrange

        boolean result = userService.isValidPassword(password);  //Act

        assertFalse(result); //Assert

    }



}