```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Exercise 1(a): where a book is placed (section + shelf).
 * Exercise 5:    also an OBSERVER that tracks which books are at this location.
 */
public class BookLocation implements LocationTracker {

    // Ex 1(a): "Once initialized, these attributes cannot be modified"
    // → private + final, and no setters.
    private final String section;
    private final int shelf;

    // Ex 5: observer state – the books currently placed here.
    // This is NOT one of the immutable attributes; it changes when the Library notifies us.
    private final List<Book> books = new ArrayList<>();

    public BookLocation(String section, int shelf) {
        this.section = section;
        this.shelf = shelf;
    }

    public String getSection() {
        return section;
    }

    public int getShelf() {
        return shelf;
    }

    // ---------- Ex 5: Observer (LocationTracker) ----------

    /**
     * Called by the Library (the subject) every time a book is added or removed.
     * The Library notifies ALL trackers, so each location ignores books that aren't placed here.
     */
    @Override
    public void update(Book book, boolean isAdded) {
        if (!this.equals(book.getLocation())) {
            return; // not our location → not our business
        }
        if (isAdded) {
            if (!books.contains(book)) { // avoid tracking the same book twice
                books.add(book);
            }
        } else {
            books.remove(book);
        }
    }

    @Override
    public Collection<Book> getBooks() {
        // Read-only view: outsiders can look, but only update() can change the list
        return Collections.unmodifiableList(books);
    }

    // ---------- equals / hashCode ----------
    // Two BookLocation objects mean the same place if section and shelf match.
    // Needed by Library.findBooksAtLocation(...) and by update(...).
    // The tracked books are deliberately left out: they are state, not identity.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLocation other = (BookLocation) o;
        return shelf == other.shelf && Objects.equals(section, other.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, shelf); // same fields as equals
    }

    @Override
    public String toString() {
        return "BookLocation{section='" + section + "', shelf=" + shelf + "}";
    }
}
```
