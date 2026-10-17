
[[2025_OOP_Exam.pdf]]

Src:
[[Book]]
[[BookLocation]]
[[DuplicateBookException]]
[[Library]]
[[LibraryObserverTest]]
[[LibraryTest]]
[[LocationTracker]]
[[Novel]]
[[SearchByAuthor]]
[[SearchByTitle]]
[[SearchStrategy]]
[[TextBook]]



- **Ex 1 – immutability:** fields that can’t change are `private final` with no setter. Only `location` in `Book` gets a setter.
- **Ex 1 – equality:** `BookLocation` overrides `equals` and `hashCode` on section + shelf. Without this, a new `BookLocation("Fiction", 1)` wouldn’t match an existing one, and both `findBooksAtLocation` and the observer would silently find nothing. This is the easiest place to lose marks.
- **Ex 2 – blocking the setter:** a subclass can’t remove an inherited method, so `TextBook` overrides `setLocation` to throw `UnsupportedOperationException`. `Novel` doesn’t override it, so its location can change.
- **Ex 3 – the collection:** books are stored in a `Map<String, Book>` keyed by ISBN, so the duplicate check is a single `containsKey`. `DuplicateBookException` extends `RuntimeException`, making it unchecked.
- **Ex 4 – Strategy:** `Library` holds a `SearchStrategy` with a setter, and `search(query)` just delegates to it.
- **Ex 5 – Observer:** `Library` keeps a list of trackers and notifies all of them on add or remove. Each `BookLocation` ignores books that aren’t at its own location. Two details:
    - A duplicate add throws _before_ notifying anyone.
    - Removing a book that isn’t there doesn’t notify anyone.

**Tests:**

- `LibraryObserverTest` is the required Exercise 5 test class (9 tests).
- `LibraryTest` is an extra class covering Exercises 1–4 (11 tests), useful for checking your own attempt.