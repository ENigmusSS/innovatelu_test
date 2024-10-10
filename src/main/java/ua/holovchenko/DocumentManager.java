package ua.holovchenko;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;


/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for store data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty() || document.getId().isBlank()) {
            String id = UUID.randomUUID().toString();
            document.setId(id);
            storage.put(id, document);
        } else {
            storage.put(document.getId(), document);
        }
        return storage.get(document.getId());
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(document -> {
                    // The less intermediate stream operations the fastest. All-in-one filter is quicker.
                    // Fastest filters first, slower will in use only when fast ones are passed.
                    // All filter battery is on single screen, they are short enough to not extract them
                    if (request.getCreatedFrom() != null) {
                        if (document.getCreated().isBefore(request.getCreatedFrom())) return false;
                    }
                    if (request.getCreatedTo() != null) {
                        if (document.getCreated().isAfter(request.getCreatedTo())) return false;
                    }
                    if (request.getTitlePrefixes() != null) {
                        String title = document.getTitle();
                        if (request.getTitlePrefixes().stream().noneMatch(title::startsWith)) return false;
                    }
                    if (request.getAuthorIds() != null) {
                        String authorID = document.getAuthor().getId();
                        if (request.getAuthorIds().stream().noneMatch(authorID::equals)) return false;
                    }
                    if (request.getContainsContents() != null) {
                        String content = document.getContent();
                        if (!request.getContainsContents().stream().allMatch(content::contains)) return false;
                        //if (request.getContainsContents().stream().noneMatch(content::contains)) return false;
                        // widening or constrictioning search criterion?
                    }
                    return true;
                })
                .toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}