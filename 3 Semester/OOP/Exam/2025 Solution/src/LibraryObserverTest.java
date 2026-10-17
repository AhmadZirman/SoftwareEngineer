import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 5: unit tests for the Observer pattern.
 * Library = subject, BookLocation = observer (LocationTracker).
 */
public class LibraryObserverTest {

    private Library library;
    private BookLocation fiction1;
    private BookLocation science2;

    @BeforeEach
    public void setUp() {
        // Fresh objects before EVERY test, so tests can't affect each other
        library = new Library();
        fiction1 = new BookLocation("Fiction", 1);
        science2 = new BookLocation("Science", 2);
        library.addTracker(fiction1);
        library.addTracker(science2);
    }

    @Test
    public void addedBookIsTrackedAtItsLocation() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");

        library.addBook(dune);

        assertEquals(1, fiction1.getBooks().size());
        assertTrue(fiction1.getBooks().contains(dune));
    }

    @Test
    public void otherLocationsIgnoreTheBook() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");

        library.addBook(dune);

        assertTrue(science2.getBooks().isEmpty()); // was notified, but filtered it out
    }

    @Test
    public void removedBookIsNoLongerTracked() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");
        library.addBook(dune);

        library.removeBook(dune);

        assertTrue(fiction1.getBooks().isEmpty());
    }

    @Test
    public void severalBooksAtSeveralLocations() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");
        Book hobbit = new Novel("222", "The Hobbit", "J.R.R. Tolkien", fiction1, "Fantasy");
        Book calculus = new TextBook("333", "Calculus", "Spivak", science2, "Mathematics");

        library.addBook(dune);
        library.addBook(hobbit);
        library.addBook(calculus);

        assertEquals(2, fiction1.getBooks().size());
        assertEquals(1, science2.getBooks().size());
        assertTrue(science2.getBooks().contains(calculus));
    }

    @Test
    public void duplicateAddDoesNotNotifyTwice() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");
        Book duneCopy = new Novel("111", "Dune (copy)", "Frank Herbert", fiction1, "Sci-Fi");
        library.addBook(dune);

        assertThrows(DuplicateBookException.class, () -> library.addBook(duneCopy));

        assertEquals(1, fiction1.getBooks().size()); // the failed add was not tracked
    }

    @Test
    public void removingUnknownBookDoesNothing() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");
        Book notInLibrary = new Novel("999", "Ghost", "Nobody", fiction1, "Mystery");
        library.addBook(dune);

        library.removeBook(notInLibrary); // must not throw

        assertEquals(1, fiction1.getBooks().size());
    }

    @Test
    public void unsubscribedTrackerIsNotNotified() {
        library.removeTracker(fiction1);
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");

        library.addBook(dune);

        assertTrue(fiction1.getBooks().isEmpty());
    }

    @Test
    public void trackerMatchesByValueNotByObject() {
        // A different BookLocation object with the same section + shelf = same place
        BookLocation sameShelf = new BookLocation("Fiction", 1);
        Book dune = new Novel("111", "Dune", "Frank Herbert", sameShelf, "Sci-Fi");

        library.addBook(dune);

        assertTrue(fiction1.getBooks().contains(dune));
    }

    @Test
    public void trackedBooksCannotBeChangedFromOutside() {
        Book dune = new Novel("111", "Dune", "Frank Herbert", fiction1, "Sci-Fi");

        assertThrows(UnsupportedOperationException.class, () -> fiction1.getBooks().add(dune));
    }
}
