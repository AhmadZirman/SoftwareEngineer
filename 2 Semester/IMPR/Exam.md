

# IMPR Simple Notes


---
## Scope

---
## Variables

---
## Conditions
```C
//If-Else:
if (x > 0) {
    // runs if condition is true
} else {
    // runs otherwise
}
```

## Loops
```c
//For-loop:
for (int i = 0; i < 5; i++) {
    // runs until i == 5
}
```

```c
// while-loop:
while (x > 0) {
    x--;
}
```
###### Common errors
- Infinite loop
- Off-by-one

## Functions
```C
int add(int a, int b){
	return a + b;
}
```
- Parameters = "formelle parametre"
- Values passed in = "Aktuelle parametre"
- Default: call by value


## Arrays
```C
int arr[3] = {1, 2, 3};
```
- Fixed size
- Continuous memory
- No bounds checking

## Struct
```C
struct Person{
	int age;
	bool isMale;
	char name[20];
}
```
- Groups related variables
###### Access:
```C
	p.age; // Value
	p->age; // Pointer
```


## Pointers
```C
int x = 10;
int *p = &x;
*p = 20;
```
- Store addresses
- ```*``` de-reference
- ```&``` address-of
##### Never
- De-reference uninitialized pointer
- Use after ```free```



## Stack vs Heap

##### Stack
- Local variables
- Auto free
- Fast

##### Heap
- ```malloc```
- Manual ```free```
- Persistent until freed


## Dynamic Memory
```C
int *a = malloc(5 * sizeof(int));
free(a);
```
##### Rules
- Every malloc ```-->``` exactly one free
- No double free
- no use-after-free