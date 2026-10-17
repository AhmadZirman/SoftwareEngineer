```java

import java.util.Objects;

/**
 * Exercise 1(b): a book in the library.
 */
public class Book {

    // Must be set at creation and never change → final, no setters
    private final String isbn;
    private final String title;
    private final String author;

    // "initialized when the object is created" but NOT marked unmodifiable → has a setter.
    // private (not protected) so subclasses must go through setLocation(), which TextBook overrides.
    private BookLocation location;

    public Book(String isbn, String title, String author, BookLocation location) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.location = location;
    }

    // ---------- getters ----------

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookLocation getLocation() {
        return location;
    }

    // ---------- setter ----------
    // Only location gets a setter: the other fields are immutable by the spec.

    public void setLocation(BookLocation location) {
        this.location = location;
    }

    // ---------- equals / hashCode ----------
    // The ISBN is the unique identifier, so two Book objects with the same ISBN are the same book.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book other = (Book) o;
        return Objects.equals(isbn, other.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author
                + "', location=" + location + "}";
    }
}
```
