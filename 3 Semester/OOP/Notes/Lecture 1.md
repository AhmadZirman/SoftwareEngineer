
## Tentative subjects across the course
- Classes
- Encapsulation
- Abstraction
- Inheritance
- Polymorphism
- Design Patterns

### Today: Classes and Objects
> [!tip] Key takeaway from today "The specific language is not important. The principles are!" OOP concepts transfer across Smalltalk, Java, C++, Python, etc. Java is just the vehicle for this course.

## Java basics
- The programming Language: Java itself is an object-oriented language.
- The Java Virtual Machine (JVM): Interprets compiled Java bytecode.
- The Java standard library: Large set of reusable components (lists, etc.).
- Slogan: "Write once. Run everywhere."

### Tool needed
-  Bare minimum: Java JDK + a plain text editor
- Nice to have:
	- IDE: IntelliJ, Eclipse, VSCode, NetBeans
	- Build Systems: Ant, Maven, Gradle


### Warm-up Java exercises (procedural, pre-OOP)
These were live-coding warm-ups to get back into Java syntax before moving to classes, not OOP examples yet:

1. Convert seconds $\to$ weeks/days/hours/min/sec
```Java
import java.util.Scanner;

public class Solution {
    // Constants for unit conversion, all derived from seconds-per-minute
    static final long SEKUNDER_PER_MINUT = 60;
    static final long SEKUNDER_PER_TIME  = SEKUNDER_PER_MINUT * 60;
    static final long SEKUNDER_PER_DAG   = SEKUNDER_PER_TIME * 24;
    static final long SEKUNDER_PER_UGE   = SEKUNDER_PER_DAG * 7;

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Indtast sekunder"); // "Enter seconds"
        long sekunderInput = scanner.nextLong();

        // Integer division (/) gets whole units, modulo (%) gets the remainder
        long uger = sekunderInput / SEKUNDER_PER_UGE;
        sekunderInput = sekunderInput % SEKUNDER_PER_UGE;
        long dage = sekunderInput / SEKUNDER_PER_DAG;
        sekunderInput = sekunderInput % SEKUNDER_PER_DAG;
        long timer = sekunderInput / SEKUNDER_PER_TIME;
        sekunderInput = sekunderInput % SEKUNDER_PER_TIME;
        long minutter = sekunderInput / SEKUNDER_PER_MINUT;
        long sekunder = sekunderInput % SEKUNDER_PER_MINUT;

        String output = String.format(
            "%d uger, %d dage, %d timer, %d minutter og %d sekunder\n",
            uger, dage, timer, minutter, sekunder
        );
        System.out.println(output);
    }
}
```

A second version (`SolutionCMD`) does the same thing but reads the seconds count from a **command-line argument** (`args[0]`) instead of `Scanner`, with a guard clauses:

```Java
if (args.length < 1) {
    System.out.println("Programmet skal have antal sekunder som parameter");
    return; // exit early if no argument was passed
}
long sekunderInput = Long.parseLong(args[0]);
```

> [!note] Goal of this exercise Practice integer devision `/` and modulo `%`. Secondary 
> Practice integer division `/` and modulo `%`. Secondary goal: get comfortable reading input both via `Scanner` and via command-line args.

2. Print a triangle pattern
Print 10 lines, each starting `|`, followed by an increasing number of `*` (0 on line 1, up to 9 on line 10):
```CMD
|
|*
|**
|***
...
|*********
```

```Java
public class Solution {
    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            System.out.write('|');
            for (int j = 0; j < i; ++j) {
                System.out.write('*'); // print i stars on row i
            }
            System.out.write('\n');
        }
    }
}
```
**Variant:** make the number of lines user-defined by reading `bound` from `args[0]` (`Integer.parseInt(args[0])`) instead of hardcoding `10`


3. Average of N integers (3 ways)
Same task, three different styles, shows how Java syntax can get progressively more compact:

```Java title:for-loop
// Classic indexed for-loop
public class Gennemsnit {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 0; i < args.length; ++i) {
            sum += Integer.parseInt(args[i]);
        }
        int gennemsnit = sum / args.length; // "gennemsnit" = average
        System.out.println(gennemsnit);
    }
}
```

```Java title:for-each-loop
// Enhanced for-each loop - cleaner, no index bookkeeping
public class GennemsnitForEach {
    public static void main(String[] args) {
        int sum = 0;
        for (String s : args) {
            sum += Integer.parseInt(s);
        }
        int gennemsnit = sum / args.length;
        System.out.println(gennemsnit);
    }
}
```

```Java title:Streams
// Streams — functional style: map each string to int, then reduce (sum) them
import java.util.Arrays;

public class GennemsnitForEachCall {
    public static void main(String[] args) {
        int sum = Arrays.stream(args)
                         .map(s -> Integer.parseInt(s))
                         .reduce(0, (a, b) -> a + b);
        int gennemsnit = sum / args.length;
        System.out.println(gennemsnit);
    }
}
```

