/**
 * Exercise 2: a textbook. Its location can NOT change after creation.
 */
public class TextBook extends Book {

    private final String subject; // set once, never changed → final, no setter

    public TextBook(String isbn, String title, String author, BookLocation location, String subject) {
        super(isbn, title, author, location); // let Book set the inherited fields
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * Book has a public setter, and a subclass can't remove an inherited method.
     * So we override it and refuse the change.
     */
    @Override
    public void setLocation(BookLocation location) {
        throw new UnsupportedOperationException("The location of a TextBook cannot be changed.");
    }

    @Override
    public String toString() {
        return "TextBook{isbn='" + getIsbn() + "', title='" + getTitle() + "', author='" + getAuthor()
                + "', location=" + getLocation() + ", subject='" + subject + "'}";
    }
}
