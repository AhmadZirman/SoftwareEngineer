import java.util.Collection;

/**
 * Exercise 5: Observer (listener) interface.
 * Anything that wants to know which books are at a location implements this.
 */
public interface LocationTracker {

    /** Called by the subject (Library) when a book is added (true) or removed (false). */
    void update(Book book, boolean isAdded);

    /** The books this tracker currently knows about. */
    Collection<Book> getBooks();
}
