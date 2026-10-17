import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extra tests for Exercises 1–4 (not required by the exam, but useful to check your own solution).
 */
public class LibraryTest {

    private Library library;
    private BookLocation fiction1;
    private BookLocation science2;
    private Book dune;
    private Book hobbit;
    private Book calculus;

    @BeforeEach
    public void setUp() {
        library = new Library();
        fiction1 = new BookLocation("Fiction", 1);
        science2 = new BookLocation("Science", 2);
        dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");
        hobbit = new Novel("222", "The Hobbit", "J.R.R. Tolkien", fiction1, "Fantasy");
        calculus = new TextBook("333", "Calculus", "Michael Spivak", science2, "Mathematics");
    }

    // ---------- Exercise 1 & 2 ----------

    @Test
    public void bookLocationsWithSameValuesAreEqual() {
        assertEquals(new BookLocation("Fiction", 1), fiction1);
        assertNotEquals(new BookLocation("Fiction", 2), fiction1);
    }

    @Test
    public void novelLocationCanChange() {
        dune.setLocation(science2);
        assertEquals(science2, dune.getLocation());
    }

    @Test
    public void textBookLocationCannotChange() {
        assertThrows(UnsupportedOperationException.class, () -> calculus.setLocation(fiction1));
        assertEquals(science2, calculus.getLocation()); // unchanged
    }

    @Test
    public void toStringIncludesAllAttributes() {
        String s = calculus.toString();
        assertTrue(s.contains("333"));
        assertTrue(s.contains("Calculus"));
        assertTrue(s.contains("Michael Spivak"));
        assertTrue(s.contains("Science"));
        assertTrue(s.contains("Mathematics"));
    }

    // ---------- Exercise 3 ----------

    @Test
    public void duplicateIsbnThrows() {
        library.addBook(dune);
        Book sameIsbn = new Novel("111", "Other title", "Someone", science2, "Drama");
        assertThrows(DuplicateBookException.class, () -> library.addBook(sameIsbn));
    }

    @Test
    public void findBooksAtLocation() {
        library.addBook(dune);
        library.addBook(hobbit);
        library.addBook(calculus);

        Collection<Book> found = library.findBooksAtLocation(new BookLocation("Fiction", 1));

        assertEquals(2, found.size());
        assertTrue(found.contains(dune));
        assertTrue(found.contains(hobbit));
    }

    @Test
    public void findBooksAtEmptyLocationReturnsEmpty() {
        library.addBook(dune);
        assertTrue(library.findBooksAtLocation(new BookLocation("History", 9)).isEmpty());
    }

    @Test
    public void removeBook() {
        library.addBook(dune);
        library.removeBook(dune);
        assertTrue(library.findBooksAtLocation(fiction1).isEmpty());
    }

    // ---------- Exercise 4 ----------

    @Test
    public void searchByTitle() {
        library.addBook(dune);
        library.addBook(hobbit);
        library.setSearchStrategy(new SearchByTitle());

        Collection<Book> found = library.search("hobbit"); // case-insensitive

        assertEquals(1, found.size());
        assertTrue(found.contains(hobbit));
    }

    @Test
    public void searchByAuthor() {
        library.addBook(dune);
        library.addBook(calculus);
        library.setSearchStrategy(new SearchByAuthor());

        Collection<Book> found = library.search("spivak");

        assertEquals(1, found.size());
        assertTrue(found.contains(calculus));
    }

    @Test
    public void searchWithNoMatchReturnsEmpty() {
        library.addBook(dune);
        library.setSearchStrategy(new SearchByAuthor());
        assertTrue(library.search("Tolstoy").isEmpty());
    }
}
