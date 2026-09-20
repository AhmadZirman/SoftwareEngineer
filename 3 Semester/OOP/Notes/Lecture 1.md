
## Tentative subjects across the course
- Classes
- Encapsulation
- Abstraction
- Inheritance
- Polymorphism
- Design Patterns

### Today: Classes and Objects
	Key takeaway from today "The specific language is not important. The principles are!" OOP concepts transfer across Smalltalk, Java, C++, Python, etc. Java is just the vehicle for this course.


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

A second version (*SolutionCMD*) does the same thing but reads the seconds count from a **command-line argument** (*args[0]*) instead of *Scanner*, with a g