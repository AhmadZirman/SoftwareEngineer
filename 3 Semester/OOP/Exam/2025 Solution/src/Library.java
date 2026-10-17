import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercise 3: manages all books.
 * Exercise 4: CONTEXT of the Strategy pattern (delegates searching to a SearchStrategy).
 * Exercise 5: SUBJECT of the Observer pattern (notifies LocationTrackers on add/remove).
 */
public class Library {

    // Map keyed by ISBN: makes the duplicate check a single lookup.
    // LinkedHashMap keeps insertion order, so results come out in a predictable order.
    private final Map<String, Book> books;

    // Ex 4: the current search strategy (defaults to title search so search() always works)
    private SearchStrategy searchStrategy = new SearchByTitle();

    // Ex 5: everyone who wants to be told about additions/removals
    private final List<LocationTracker> trackers = new ArrayList<>();

    /** Ex 3: empty constructor that starts with an empty collection. */
    public Library() {
        this.books = new LinkedHashMap<>();
    }

    // ---------- Exercise 3 ----------

    public void addBook(Book book) {
        if (books.containsKey(book.getIsbn())) {
            // Throw BEFORE changing anything, so no tracker is told about a book that wasn't added
            throw new DuplicateBookException(book.getIsbn());
        }
        books.put(book.getIsbn(), book);
        notifyTrackers(book, true); // Ex 5
    }

    public void removeBook(Book book) {
        Book removed = books.remove(book.getIsbn()); // returns null if it wasn't there
        if (removed != null) {
            notifyTrackers(removed, false); // Ex 5: only notify if something actually changed
        }
        // Not found → do nothing (as required)
    }

    public Collection<Book> findBooksAtLocation(BookLocation location) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (location.equals(book.getLocation())) { // uses BookLocation.equals (section + shelf)
                result.add(book);
            }
        }
        return result; // empty list if no match
    }

    // ---------- Exercise 4: Strategy ----------

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    /** Library doesn't know HOW to search: it hands the job to the current strategy. */
    public Collection<Book> search(String query) {
        return searchStrategy.search(query, books.values());
    }

    /** Convenience overload: pick the strategy for just this one search. */
    public Collection<Book> search(String query, SearchStrategy strategy) {
        return strategy.search(query, books.values());
    }

    // ---------- Exercise 5: Observer ----------

    public void addTracker(LocationTracker tracker) {
        trackers.add(tracker); // "subscribe"
    }

    public void removeTracker(LocationTracker tracker) {
        trackers.remove(tracker); // "unsubscribe"
    }

    private void notifyTrackers(Book book, boolean isAdded) {
        for (LocationTracker tracker : trackers) {
            tracker.update(book, isAdded); // each tracker decides if the book concerns it
        }
    }
}
