package dk.kea.library.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.kea.library.dto.BookResponse;
import dk.kea.library.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /** Search is open to everyone, no login needed. */
    @GetMapping
    public List<BookResponse> search(@RequestParam(name = "q", required = false) String query) {
        return bookService.search(query);
    }

    @GetMapping("/{workId}")
    public BookResponse getBook(@PathVariable String workId, HttpSession session) {
        SessionUser.requireUserId(session);
        return bookService.getBook(workId);
    }
}
