```java
import java.util.Collection;

/**
 * Exercise 4: Strategy interface. Each implementation is one way of searching.
 */
public interface SearchStrategy {
    Collection<Book> search(String query, Collection<Book> books);
}
```