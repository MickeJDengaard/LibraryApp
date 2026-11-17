package dk.kea.library.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dk.kea.library.exception.OpenLibraryUnavailableException;

/** Calls https://openlibrary.org (or OPENLIBRARY_BASE_URL) over HTTP. */
@Component
public class HttpOpenLibraryClient implements OpenLibraryClient {

    private static final String WORKS_PREFIX = "/works/";
    private static final int TIMEOUT_MILLIS = 5000;

    private final RestClient restClient;

    public HttpOpenLibraryClient(RestClient.Builder builder,
                                 @Value("${openlibrary.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(TIMEOUT_MILLIS);
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "LibraryApp/1.0 (student project)")
                .build();
    }

    @Override
    public List<OpenLibraryBook> search(String query, int limit) {
        try {
            SearchResponse response = restClient.get()
                    .uri("/search.json?q={q}&limit={limit}", query, limit)
                    .retrieve()
                    .body(SearchResponse.class);
            List<OpenLibraryBook> books = new ArrayList<>();
            if (response == null || response.docs() == null) {
                return books;
            }
            for (SearchDoc doc : response.docs()) {
                books.add(new OpenLibraryBook(toWorkId(doc.key()), doc.title(),
                        doc.authorNames() == null ? List.of() : doc.authorNames(),
                        doc.firstPublishYear()));
            }
            return books;
        } catch (RestClientException e) {
            throw new OpenLibraryUnavailableException();
        }
    }

    @Override
    public Optional<OpenLibraryBook> findWork(String workId) {
        try {
            WorkResponse work = restClient.get()
                    .uri("/works/{id}.json", workId)
                    .retrieve()
                    .body(WorkResponse.class);
            if (work == null || work.title() == null) {
                return Optional.empty();
            }
            return Optional.of(new OpenLibraryBook(workId, work.title(), List.of(), null));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new OpenLibraryUnavailableException();
        }
    }

    /** "/works/OL45883W" becomes "OL45883W" */
    private static String toWorkId(String key) {
        if (key != null && key.startsWith(WORKS_PREFIX)) {
            return key.substring(WORKS_PREFIX.length());
        }
        return key;
    }

    // The parts of Open Library's JSON that we use

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SearchResponse(List<SearchDoc> docs) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SearchDoc(String key,
                     String title,
                     @JsonProperty("author_name") List<String> authorNames,
                     @JsonProperty("first_publish_year") Integer firstPublishYear) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record WorkResponse(String title) {
    }
}
