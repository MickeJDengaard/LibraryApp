package unitTests;

import dk.kea.library.service.BookService;
import dk.kea.library.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

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
            "valid-query123456",
            "Valid_query_due_to_length_this_query_is_99_characters_long_currently_which_means_it_looks_stupid123",
            "Valid_query_due_to_length_this_query_is_100_characters_long_currently_which_means_it_looks_stupid123"

    })
    void isValidQueryPositiveTest(String query) {
        BookService bookService = new BookService(); //Arrange

        boolean result = bookService.isValidQuery(query);  //Act

        assertTrue(result); //Assert

    }

    //Negative
    @ParameterizedTest
    @CsvSource(value = {
            "''", //Empty string
            "i",
            "in",
            "Invalid_query_due_to_length_this_query_is_101_characters_long_currently_which_means_it_looks_stupid12",
            "Invalid_query_due_to_length_this_query_is_102_characters_long_currently_which_means_it_looks_stupid123",
            "null"
    }, nullValues = {"null"})
    void isValidQueryNegativeTest(String query) {
        BookService bookService = new BookService(); //Arrange

        boolean result = bookService.isValidQuery(query);  //Act

        assertFalse(result); //Assert

    }
}