```java
/**
 * Exercise 2: a novel. Its location CAN change, so it simply keeps Book's setLocation().
 */
public class Novel extends Book {

    private final String genre; // set once, never changed → final, no setter

    public Novel(String isbn, String title, String author, BookLocation location, String genre) {
        super(isbn, title, author, location);
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    // No setLocation override needed: the inherited one from Book already allows changes.

    @Override
    public String toString() {
        return "Novel{isbn='" + getIsbn() + "', title='" + getTitle() + "', author='" + getAuthor()
                + "', location=" + getLocation() + ", genre='" + genre + "'}";
    }
}
```