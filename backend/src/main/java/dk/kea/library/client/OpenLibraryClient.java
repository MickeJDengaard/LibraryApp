package dk.kea.library.client;

import java.util.List;
import java.util.Optional;

/**
 * Gateway to the Open Library API. It is an interface so it can be
 * replaced with a mock or a stub in tests.
 */
public interface OpenLibraryClient {

    /** Searches books. Throws OpenLibraryUnavailableException if Open Library cannot be reached. */
    List<OpenLibraryBook> search(String query, int limit);

    /** Finds one work by its id (e.g. OL45883W). Empty if Open Library does not know it. */
    Optional<OpenLibraryBook> findWork(String workId);
}
