---

excalidraw-plugin: parsed
tags: [excalidraw]

---
==⚠  Switch to EXCALIDRAW VIEW in the MORE OPTIONS menu of this document. ⚠== You can decompress Drawing data with the command palette: 'Decompress current Excalidraw file'. For more info check in plugin settings under 'Saving'


# Excalidraw Data

## Text Elements
Step 1: Simplify each term:

    Term 1: 

    Term 2: ^4UWIBlZq

(Since lg is log base 2, and log 2 = 1) ^baRDg0kO

Term 3: ^iE0MkaSm

Step 2: Identify the dominant term ^hrhveQiU

Comparing all terms:

The dominant term is clearly: ^UG3AVYEA

So the whole expression is: ^7xDND2LX

Why?

a) n^5 grows slower than (n^5 lg n), so this upper bound holds

b) n^5 grows faster than n, so this lower bound holds

c) The dominant term is n^5, not n^(4.5)

d) The (lg n) factor isn't present in the dominant term

e) This is exactly the tight bound ^nknixYGZ

Step 1: Simplify each term ^xQriBELh

term 1:

term 2:

term 3:

term 4: ^bEjWlG54

Step 2: Dominant term ^V5ygx4DT

All terms combined to:

dominant term is:  ^aEQc22LL

Here, we have two variables, n and m, so we need to be careful. M is independent of n
The terms don't need simplification.

n^2 will always dominate 100n. But n lg m vs n^2 depends on m.
If m is huge, n lg m could be significant. So the tight bound keeping both relevant terms is Θ(n lg m + n^2). ^cHzAEvxP

Recurrence: Q(n) = Q(n/2)/4 + 2n

To apply the Master Theorem, we need the form T(n) = aT(n/b) + f(n) where a ≥ 1

Here:
    . a = 1/4, because Q(n/2) is divided by 4
    . b = 2
    . f(n) = 2n

The problem is a = 1/4 < 1, which violates the Master Theorem requirement that a ≥ 1.
The Master Theorem simply does not apply here ^0gZ4TuLa

The key insight is simple: once you know Q(n) = Θ(n), any function that n dominates is a valid  Ω, and any function that dominates n is a valid O. Θ(n lg n) is wrong because it claims both simultaneously, which contradicts Θ(n) ^xBaloyiy

What is T(n)?
    

What does        mean here?
    We need to show that                   for some constant          and all ^K0GtwIZI

Substitution Method
    You guess                , then prove it holds by assuming it works for n-2 (inductive hypothesis) and showing it follows for n ^9Rx14QqE

Base Cases (n_0 = 3)


This holds for



This holds for ^715lMyi8

Inductive Step (n > 4)
    Assume ^PaolkjOn

Conclusion:
    

They key trick in the inductive step it that since n > 4, we can safely replace n with 4, which is exactly 2^2, letting us fold it into the exponent and complete the proof ^j30HRrux

a) Both are Θ(n^2) in the worst case.
    - Quicksort worst case: Already sorted array, pivot always picks the smallest/largest element,
    degenerates into n recursive calls each doing n work
    - Insertion Sort worst case: Reverse sorted array, every element has to shift all the way left

b) Max-Heapify works in place (it just swaps elements within the array), but Merge Sort requires an auxiliary array to merge two halves, so it does NOT work in place.

c) Counting Sort only works on non-negative integers. If the array contains negative numbers like -1, -2, it breaks down because it uses the values as array indices, and negative indices don't exist.

d) That's only true for a balanced BST (Binary Search Tree). If the tree is unbalanced (e.g. you insert already sorted elements), it degenerates into a linked list and insertion takes Θ(n).

e) Red-black trees enforce balance by construction through their colouring rules and rotations, guaranteeing height               , so insertion is always Θ(lg n). ^zYLebBIs

        10
       /  \
      5    20
     / \
    1   7 ^kqTjXijJ

As a binary tree it looks like this (index 1 = root):

a) BST (Binary Search Tree) property says child < parent < right child.
    - 10's right child is 20
    - 5's left child is 1, right child is 7
    - 5 is in 10's left subtree, and 20 is in the right. All values in left subtree (5, 1, 7) are less than 10


b) Build-Max-Heap runs Max-Heapify bottom up. The correst result is [20, 7, 10, 1, 5]

c) Max-Heapify(A, 2) looks at index 2 (values 5) and its children 1 and 7, swaps 5 with 7.
    Results is [10, 7, 20, 1, 5]. The answer listed has 7 duplicates and 5 missing, which is clearly wrong.

d) Max-Heap requires every parent >= its children. Here the root is 10 but 20 is its right child, and 20 > 10,
    which violates the property immediately ^s0QMaP50

Insert k = 7, linear probing:





Table becomes: [14, 71, 29, 7, 32, 75, Nil] ^pFmWyLYl

The hash functions are:
 
    - Linear: 

    - Quadratic: 

    - Double Hashing:  ^YpWHeK3K

Insert k = 14, quadratic probing, c_1 = 2, c_2 = 4



Table becomes: [14, 71, 29, Nil, 32, 75, 14] ^k7urHWPc

Insert K = 7, double hashing ^oI6Wl1l5

Table becomes [14, 71, 29, Nil, 32, 75, 7] ^UxZapQbN

DFS on G:
    Edges: 1 → 5, 1 → 6, 5 → 2, 5 → 3, 2 → 4, 3 → 2, 4 → 1, 4 → 6
Rule: Always pick smallest vertex label first. ^SsvZzLtR

Running DFS:
Visit 1 → d=1
    - Neighbours: 5, 6

Visit 5 → d=2
    - Neighbours: 2, 3

Visit 2 → d=3
    - Neighbour: 4

Visit 4 → d=4
    - Neighbour: 1, 6

Visit 1 → already visited, skip
Visit 6 → d=5
    - no unvisited neighbours
    - finish 6 → f=6
    - finish 4 → f=7
    - finish 2 → f=8

Visit 3 → d=9 (next unvisited neighbour of 5)
    - neighbour: 2 → already visited, skip
    - finish 3 → f=10
    - finish 5 → f=11
    - back to 1, neighbour 6 → already visited, skip
    - finish 1 → f=12 ^ErW13qRq

## Embedded Files
63acbc708b0310ffb6899dbe4f62b1b57acefe81: $$\color{black}n^4⋅n^{\frac{1}{5}}=n^{4+\frac{1}{5}}=n^{\frac{21}{5}}=n^{4.2}$$

0dd52dc161ac53fccf309f824cbe04783d4209e0: $$\color{black}lg\space 2^{n^5} = n^5 \cdot lg\space 2 = n^5 \cdot 1 = n^5$$

a4f82ab09f6e018196dea6f4238949dde2cefd24: $$\color{black}n(n^3 + n \space lg\space n) = n^4 + n^2\space lg\space n$$

8b5a0d8008db92cd939117771e0a0bc5c8bbf772: $$\color{black} n^{4.2}, \space n^5, \space n^4, \space n^2\space lg\space n$$

1fae626164fe31b08cd8748e8ec72d6752b38962: $$\color{black} n^5$$

3fde414768439b95bf61c0bc6a0ce64562e8cd4d: $$\color{black} \Theta({n^5})$$

0f5d22499763dc28cc270c919fe919d7d0915f23: $$\color{black}n\lg 2^{\lg n} = n\lg n$$

4ec685f274d97f55815d479be190b68501be0b1e: $$\color{black}n\lg n^{2000} = 2000n\lg n$$

7be3698dd470ae9c5fda0ae7703b674e495f3b31: $$\color{black}n$$

94af508a39da71bc61ab1d3e38b42d6281d30966: $$\color{black}2000$$

e93dd036e9c7d99c3d510dbd31304defae31b11e: $$\color{black} n\lg n + n + 2000n\lg n + 2000$$

4b7fa2e8562ec64216654fcaa11de25429b4e849: $$\color{black} n\lg n$$

deebcce4c66f9cd02d4d4adfc51cd9aba7ebf953: $$\color{black} T(n) = \begin{cases} 1 & \text{if } n \in {0, 1} \ n \cdot T(n-2) & \text{if } n > 1 \end{cases}$$

8c5671e2ea563a651de2aa66db1a4658a07d49ec: $$\color{black} \Omega(2^n)$$

60364ee6b405422459f4dbf61166e70b73f9dde8: $$\color{black} T(n) = \geq c \cdot 2^n$$

f4875f1b7a632bd551ec77ac3a38b07385b1e957: $$\color{black} c > 0$$

8df05507b323bcc07d6967aee4170caf935b5636: $$\color{black} n \geq n_{0}$$

59940e2683fe0d91e3461544e8ada70fe455b7c1: $$\color{black} T(n) \geq c \cdot 2^n$$

35f3b77bd2f17af07efc43a8a47419609568b6a1: $$\color{black} n = 3: T(3) = 3 \cdot T(1) = 3 \cdot 1 = 3 \geq c \cdot 2^3 = 8c$$

06326fa788bc768e767c9795eda0e2fb6bd2d0ce: $$\color{black} c \leq \frac{3}{8}$$

f3d00be72cd3958d6a6b521e50634e342371b71f: $$\color{black}n = 4: T(4) = 4 \cdot T(2) = 4 \cdot 2 \cdot T(0) = 8 \geq c \cdot 2^4 = 16c$$

d9e932527f8e8005dd3e53fd1b7a8a25d2585643: $$\color{black} c \leq \frac{1}{2}$$

3ea485c18e78996acf4cdc317c11bf7e91375239: $$\color{black} T(n-2) \geq c \cdot 2^{n-2} \text{ (inductive hypothesis). Then:}$$

4c6c33f0ee96fc4828186369e396f02afdb77cf5: $$\color{black} T(n) = n \cdot T(n-2)$$

bcb66ae9d2647c12d2da32acd08644b0533041bd: $$\color{black} \geq n d\cot c \cdot 2^{n-2}$$

ca2bd409df947719777f680b95a0a706d3a61a0b: $$\color{black} \geq 4 \cdot c \cdot 2^{n-2} \quad \text{(since } n>4 \text{)}$$

1999d7e444ec3a5245433534fe3b6d6c2728be1c: $$\color{black} = c \cdot 2^2 \cdot 2^{n-2}$$

bdf29b4ce33ee177fc00993732dc3ed77e14792f: $$\color{black} = c \cdot 2^n \checkmark$$

480d9226f6e46f323ab7bde1272700338f005a0a: $$\color{black} \text{ By choosing } 0 < c \leq 3/8 \text{ all cases hold, so yes we can prove } T(n) = \Omega(2^n)$$

9c55370f3fee7c96900f3ce075c18c18395b96e3: $$\color{black} \leq 2\lg(n+1)$$

ba3a517592795c5b6699a9343678a784144b5e57: $$\color{black} h(7, 0) = (7 + 0) \mod 7 = 0 → T[0] = 14, \text{ occupied}$$

57b7cf8adec662c606c49f96f357aee0cbf11771: $$\color{black} h(7, 1) = (7 + 1) \mod 7 = 1 → T[1] = 71, \text{ occupied}$$

144898bc8457374d9b719a81c79e333f0cb52b60: $$\color{black} h(7, 2) = (7 + 2) \mod 7 = 2 → T[2] = 29, \text{ occupied}$$

2e9496c68749c682d66e1fbe05551c26628f0775: $$\color{black} h(7, 3) = (7 + 3) \mod 7 = 3 → T[3] = Nil \space \space \checkmark \text{ insert here}$$

700277d3722114085fa5ae733974ddcecff060e9: $$\color{black} h(k, i) = (k + c_1 i + c_2 i^2) \mod m$$

ea487d7dab7c20096ed870880125a46f25879d26: $$\color{black} h(k, i) = (h_1(k) + i \cdot h_2(k)) \mod m$$

ba557f2f5db2ff8087195802ccf01fa2280920f1: $$\color{black} h(k, i) = (k + i) \mod m$$

2e1be14335f7cb2a4048e6e580891e5819bc4d56: $$\color{black} h(14, 0) = (14 + 0 + 0) \mod 7 = 0 → T[0] = 14, occupied$$

8850173894dcc7f36994db45e7907eae310a8d77: $$\color{black} h(14, 1) = (14 + 2\cdot1 + 4\cdot1^2) \mod 7 = (14 + 2 + 4) \mod 7 = 20 \mod 7 = 6 → T[6] = Nil \space \space \checkmark insert \space here$$

fe48511697d544ff80dde2f67e65c8365afa9afe: $$\color{black}h_{1}(k) = k = 7$$

c4ab8ce20fee78f207ed6e4e7e22dbc3a2d0f381: $$\color{black} h_{2}(k) = 1 (7\mod 6) = 1 + 1 = 2$$

2e2361879eaf1c53a96287cbdd5606321ce37ac4: $$\color{black} h(7, 0) = (7 + 0\cdot2) \mod 7 = 0 → T[0] = 14, occupied$$

4c87f77436ef31f15337463f243953db662792d1: $$\color{black} h(7, 1) = (7 + 1\cdot2) \mod 7 = 9 \mod 7 = 2 → T[2] = 29, occupied$$

3a27efe60276a880981d01ceb3f1f90d3bb6cce4: $$\color{black} h(7, 2) = (7 + 2\cdot2) \mod 7 = 11 \mod 7 = 4 → T[4] = 32, occupied$$

1ef081326897a77e180bdbca0fd15fa6e554a97d: $$\color{black} h(7, 3) = (7 + 3\cdot2) \mod 7 = 13 \mod 7 = 6 → T[6] = Nil \space \space \checkmark insert \space here$$

9d9f24442efc63cdda558c188130fce3fb8b83e1: [[Pasted Image 20260605145354_821.png]]

7a24e0c1f2f9daa98815bbead19bd2820d0c30db: [[Pasted Image 20260605145400_074.png]]

a9c4b23dddce74962857d35a8b3c58661a47c1ac: [[Pasted Image 20260605151539_086.png]]

a074aa6e0b7a11f3886814b6bfb2b1416e09dd4e: [[Pasted Image 20260605171846_865.png]]

74c549077e510d2d476d5aa42e17e30ab1e4e3a2: [[Pasted Image 20260605173449_394.png]]

2b6e1f57de0116d7fd80d9502a7deed7e0c0f462: [[Pasted Image 20260609215726_296.png]]

a429815d9d952052d4acd8e404dfeca9f6fda71c: [[Pasted Image 20260609220253_663.png]]

46eb54e608366bd5441331d1e19a5819fbe42293: [[Pasted Image 20260609225926_921.png]]

48526b84f97ca51927750ee84528ad12442a98b1: [[Pasted Image 20260609231831_259.png]]

2917fc7d417dac6da2f916409f596df011670eba: [[Pasted Image 20260609233222_252.png]]

f04b619b970459332ac628b5ce5ebd63e2ba9bbb: [[Pasted Image 20260609234012_008.png]]

4f9ce31d39c01e44cdfce5de3afc185a2acc8fc6: [[Pasted Image 20260609235540_031.png]]

2a4ae832740f57105a22f03bf42527eb4e82273b: [[Pasted Image 20260610003747_790.png]]

56cde7d8724e197e22a5165f1a5a2aa4276c6ef0: [[Pasted Image 20260610012023_398.png]]

f6ccd21d5523f2102dbbaa8fa4f77df28931d51b: [[Pasted Image 20260610012203_852.png]]

%%
## Drawing
```compressed-json
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

AFEgQAA=
```
%%