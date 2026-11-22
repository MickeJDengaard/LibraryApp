package IntegrationTests;

import dk.kea.library.LibraryApplication;
import dk.kea.library.client.OpenLibraryBook;
import dk.kea.library.dto.BookResponse;
import dk.kea.library.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.awt.print.Book;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes= LibraryApplication.class)
@Import(Postgresconfig.class)
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void search() {
        List<BookResponse> result = bookService.search("Java");

        assertEquals(20, result.size());
    }

    @Test
    void getBook() {
        BookResponse foundBook = bookService.getBook("OL3372608W");

        assertEquals("Java for dummies", foundBook.title());
    }
}