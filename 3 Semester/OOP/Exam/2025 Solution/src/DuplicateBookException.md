```java
/**
 * Exercise 3: thrown when a book with the same ISBN is already in the library.
 *
 * Extends RuntimeException → unchecked, so callers aren't forced to catch it.
 * (Extending Exception instead would make it checked and require "throws" on addBook.)
 */
public class DuplicateBookException extends RuntimeException {

    private final String isbn;

    public DuplicateBookException(String isbn) {
        super("A book with ISBN " + isbn + " already exists in the library."); // getMessage()
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
```
