package dk.kea.library.client;

import java.util.List;

/** A book as Open Library describes it. firstPublishYear can be null. */
public record OpenLibraryBook(String workId, String title, List<String> authors, Integer firstPublishYear) {
}
