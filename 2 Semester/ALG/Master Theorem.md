
## What is recurrence?
A recurrence is just a function defined in terms of itself. For example $Q(n) = Q\left( \frac{n}{2} \right)/4 + 2n$ means:
"to solve a problem of size n, solve a smaller problem of size n/2, then do 2n extra work."

---
## What is the Master Theorem?
It's a shortcut formula for solving recurrences of this specific shape:
$T(n) = a \cdot T\left( \frac{n}{b} \right) +f(n)$

Where:
- a = how many subproblems you split into
- b = how much smaller each subproblem is
- f(n) = extra work done outside the recursive call
The MT compares f(n) against $n^{\log_{b}a}$ (called the "watershed function") to decide which dominates, and gives you the tight bound $\Theta$ automatically

---

### The 3 Cases
Let $p = \log_{b} a$ (the watershed component). Then:
- **Case 1:** f(n) grows *slower* than $n^p \rightarrow \Theta(n^p)$
- **Case 2:** f(n) grows *at the same rate* as $n^p \rightarrow \Theta(n^p \lg n)$
- **Case 3:** f(n) grows *faster* than $n^p \rightarrow \Theta(f(n))$

---

### Applying it to F22, Q2.1: Q(n)

$Q(n) = \begin{cases} \Theta(1) & \text{if  } n = 1 \\ (Q(n/2) + 8n)/4 & \text{if  } n > 1 \end{cases}$

Rewrite the recurrence clearly:
$Q(n) = \frac{1}{4} Q\left(\frac{n}{2} \right) + 2n$

So:
- a = 1/4
- b = 2
- f(n) = 2n

The Master Theorem has one hard requirement: $a \geq 1$, because you need at least one subproblem to recursive into. Here a a = 1/4 which means you're not even making one full recursive call, that breaks the whole premise of the theorem.

#### How to solve?
Via substitution method, you guess the answer and prove it. The official solution proves Q(n) = $\Theta(n)$ that way, meaning the 2n extra work dominates since the recursive part shrinks so fast it barely contributes-