package dk.kea.library.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dk.kea.library.client.OpenLibraryBook;
import dk.kea.library.client.OpenLibraryClient;
import dk.kea.library.dto.BookResponse;
import dk.kea.library.exception.InvalidSearchException;
import dk.kea.library.exception.NotFoundException;
import dk.kea.library.model.ReservationStatus;
import dk.kea.library.repository.LoanRepository;
import dk.kea.library.repository.ReservationRepository;

@Service
public class BookService {

    public static final int QUERY_MIN_LENGTH = 2;
    public static final int QUERY_MAX_LENGTH = 100;
    public static final int MAX_RESULTS = 20;

    private OpenLibraryClient openLibrary;
    private LoanRepository loans;
    private ReservationRepository reservations;

    public BookService(OpenLibraryClient openLibrary, LoanRepository loans, ReservationRepository reservations) {
        this.openLibrary = openLibrary;
        this.loans = loans;
        this.reservations = reservations;
    }

    public BookService() {

    }


    public List<BookResponse> search(String query) {
        if (!isValidQuery(query)) {
            throw new InvalidSearchException(); // BR-11
        }
        List<OpenLibraryBook> found = openLibrary.search(query, MAX_RESULTS);

        List<BookResponse> result = new ArrayList<>();
        for (OpenLibraryBook book : found) {
            if (result.size() == MAX_RESULTS) {
                break; // BR-11: at most 20 results
            }
            result.add(withFlags(book));
        }
        return result;
    }

    public BookResponse getBook(String workId) {
        OpenLibraryBook book = openLibrary.findWork(workId).orElseThrow(NotFoundException::new);
        return withFlags(book);
    }

    /** BR-11: 2-100 characters */
    public boolean isValidQuery(String query) {
        if (query == null) {
            return false;
        }
        int length = query.length();
        return length >= QUERY_MIN_LENGTH && length <= QUERY_MAX_LENGTH;
    }

    private BookResponse withFlags(OpenLibraryBook book) {
        boolean available = !loans.existsByWorkIdAndReturnedOnIsNull(book.workId());
        boolean reserved = reservations.existsByWorkIdAndStatus(book.workId(), ReservationStatus.ACTIVE);
        return new BookResponse(book.workId(), book.title(), book.authors(), book.firstPublishYear(),
                available, reserved);
    }
}
