package ua.holovchenko;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.holovchenko.DocumentManager.Author;
import ua.holovchenko.DocumentManager.Document;
import ua.holovchenko.DocumentManager.SearchRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private static final List<Document> testList = new ArrayList<>(3);

    @BeforeAll
    static void testData() {
        testList.add(
                Document.builder()
                        .id("Test1")
                        .title("TestDoc1")
                        .content("TestingSomething")
                        .created(Instant.now())
                        .author(Author.builder().id("AuthorID1").name("John Doe").build())
                        .build()
        );
        testList.add(
                Document.builder()
                        .id("  ")
                        .title("Test2")
                        .content("LoremIpsumWhatever")
                        .created(Instant.MIN)
                        .author(Author.builder().id("Smith").name("John Smith").build())
                        .build()
        );
        testList.add(
                Document.builder()
                        .title("Teeeest3")
                        .content("Vasya Pupking Fun Club")
                        .created(Instant.MAX)
                        .author(Author.builder().id("VPFC").name("Vasya Pupkin").build())
                        .build()
        );
        testList.add(
                Document.builder()
                        .id("")
                        .title("LoremIpsum")
                        .content("LoremIpsumDolorSitAmeting")
                        .created(Instant.now())
                        .author(Author.builder().id("Latin").name("Adeptus Mechanicus").build())
                        .build()
        );
    }

    @Test
    void save_WithId() {
        //Arrange
        DocumentManager manager = new DocumentManager();

        //Act
        Document saved = manager.save(testList.get(0));

        //Assert
        assertEquals(testList.get(0), saved);
    }

    @Test
    void save_WithBlankId() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        Document testDoc = testList.get(1);

        //Act
        Document saved = manager.save(testDoc);

        //Assert
        assertEquals(testDoc.getTitle(), saved.getTitle());
        assertEquals(testDoc.getContent(), saved.getContent());
        assertEquals(testDoc.getCreated(), saved.getCreated());
        assertEquals(testDoc.getAuthor(), saved.getAuthor());
        assertFalse(saved.getId() == null || saved.getId().isBlank() || saved.getId().isEmpty());
    }

    @Test
    void save_WithoutId() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        Document testDoc = testList.get(2);

        //Act
        Document saved = manager.save(testDoc);

        //Assert
        assertEquals(testDoc.getTitle(), saved.getTitle());
        assertEquals(testDoc.getContent(), saved.getContent());
        assertEquals(testDoc.getCreated(), saved.getCreated());
        assertEquals(testDoc.getAuthor(), saved.getAuthor());
        assertFalse(saved.getId() == null || saved.getId().isBlank() || saved.getId().isEmpty());
    }

    @Test
    void save_WithEmptyId() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        Document testDoc = testList.get(3);

        //Act
        Document saved = manager.save(testDoc);

        //Assert
        assertEquals(testDoc.getTitle(), saved.getTitle());
        assertEquals(testDoc.getContent(), saved.getContent());
        assertEquals(testDoc.getCreated(), saved.getCreated());
        assertEquals(testDoc.getAuthor(), saved.getAuthor());
        assertFalse(saved.getId() == null || saved.getId().isBlank() || saved.getId().isEmpty());
    }

    @Test
    void search_titlePrefix() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> prefixes = new ArrayList<>();
        prefixes.add("TestDoc");
        SearchRequest request = SearchRequest.builder().titlePrefixes(prefixes).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(testList.get(0), found.get(0));
        assertEquals(1, found.size());
    }

    @Test
    void search_titlePrefixes() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> prefixes = new ArrayList<>();
        prefixes.add("TestDoc");
        prefixes.add("Lorem");
        SearchRequest request = SearchRequest.builder().titlePrefixes(prefixes).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(2, found.size());
    }

    @Test
    void search_authorId() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> authors = new ArrayList<>();
        authors.add("AuthorID1");
        SearchRequest request = SearchRequest.builder().authorIds(authors).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(1, found.size());
        assertEquals(testList.get(0), found.get(0));
    }

    @Test
    void search_authorIds() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> authors = new ArrayList<>();
        authors.add("AuthorID1");
        authors.add("Latin");
        SearchRequest request = SearchRequest.builder().authorIds(authors).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(2, found.size());
        assertEquals(testList.get(0), found.get(0));
        assertEquals(testList.get(3).getAuthor(), found.get(1).getAuthor());
    }

    @Test
    void search_authorIdsAndTitlePrefix_BothCriteriaMatchOnly() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> authors = new ArrayList<>();
        authors.add("AuthorID1");
        authors.add("Latin");
        List<String> prefixes = new ArrayList<>();
        prefixes.add("Test");
        SearchRequest request = SearchRequest.builder().authorIds(authors).titlePrefixes(prefixes).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(1, found.size());
        assertEquals(testList.get(0), found.get(0));
    }

    @Test
    void search_ContentContains() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> search = new ArrayList<>();
        search.add("ingSome");
        SearchRequest request = SearchRequest.builder().containsContents(search).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(1, found.size());
        assertEquals(testList.get(0), found.get(0));
    }

    @Test
    void search_ContentContainsMultipleResults() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        List<String> search = new ArrayList<>();
        search.add("Ipsum");
        SearchRequest request = SearchRequest.builder().containsContents(search).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(2, found.size());
    }

    @Test
    void search_createdFrom() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        SearchRequest request = SearchRequest.builder().createdFrom(Instant.now()).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(1, found.size());
        assertEquals(testList.get(2).getTitle(), found.get(0).getTitle());
    }

    @Test
    void search_createdFrom_MultipleResults() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        SearchRequest request = SearchRequest.builder().createdFrom(Instant.MIN.plusSeconds(1000)).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(3, found.size());
    }

    @Test
    void search_createdTo() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        SearchRequest request = SearchRequest.builder().createdTo(Instant.now().minusSeconds(1000000)).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(1, found.size());
        assertEquals(testList.get(1).getTitle(), found.get(0).getTitle());
    }

    @Test
    void search_createdTo_MultipleResults() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        SearchRequest request = SearchRequest.builder().createdTo(Instant.now()).build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(3, found.size());
    }

    @Test
    void search_createdFromTo_MultipleResults() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);
        SearchRequest request = SearchRequest.builder()
                .createdFrom(Instant.MIN.plusSeconds(1000))
                .createdTo(Instant.MAX.minusSeconds(1000))
                .build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(2, found.size());
    }

    //ONE_COMBINATORY_BLAST_LATER

    @Test
    void search_AllFilters() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);

        List<String> authors = new ArrayList<>();
        authors.add("AuthorID1");
        authors.add("Latin");

        List<String> prefixes = new ArrayList<>();
        prefixes.add("Test");

        List<String> search = new ArrayList<>();
        search.add("ing");

        SearchRequest request = SearchRequest.builder()
                .createdFrom(Instant.MIN.plusSeconds(1000))
                .createdTo(Instant.MAX.minusSeconds(1000))
                .authorIds(authors)
                .titlePrefixes(prefixes)
                .containsContents(search)
                .build();

        //Act
        List<Document> found = manager.search(request);

        //Assert
        assertEquals(1, found.size());
        assertEquals(testList.get(0), found.get(0));
    }

    @Test
    void findById_correctId() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);

        //Act
        Document found = manager.findById("Test1").get();

        //Assert
        assertEquals(testList.get(0), found);
    }

    @Test
    void findById_incorrectId_returnsOptionalEmpty() {
        //Arrange
        DocumentManager manager = new DocumentManager();
        testList.forEach(manager::save);

        //Act
        Optional<Document> found = manager.findById("qwerty");

        //Assert
        assertEquals(Optional.empty(), found);
    }
}