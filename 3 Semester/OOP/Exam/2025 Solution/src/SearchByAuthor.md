import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Exercise 4: concrete strategy – match books whose author contains the query (case-insensitive).
 */
public class SearchByAuthor implements SearchStrategy {

    @Override
    public Collection<Book> search(String query, Collection<Book> books) {
        List<Book> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(q)) {
                result.add(book);
            }
        }
        return result; // empty list if nothing matches, never null
    }
}
