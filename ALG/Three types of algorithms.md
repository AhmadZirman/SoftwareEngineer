
# Sorting
A sorting algorithm is a method or a process used to rearrange elements in a list or an array in a certain order, whether it be ascending, descending, or even based on some complex rules.

**Merge Sort**
Merge Sort is an efficient, stable, and comparison-based Divide and Conquer sorting algorithm, and its recursive. It divides the input array into two halves, call itself for the two halves, sorting them, and then merges the two sorted halves.
 ```python
def merge_sort(arr):
	if len(arr) > 1:
		mid = len(arr)
		L = arr[:mid]
		R = arr[mid:]
		 
		merge_sort(L)
		merge_sort(R)
		 
		merge(arr, L, R)
 ```
 
```python
def merge(arr, L, R):
	i = j = k = 0
	
	# Merging the temporary arrays
	# back into arr[]
	while i < len(L) and j < len(R):
		if L[i] < R[j]:
			arr[k] = L[i]
			i += 1
		else:
			arr[k] = R[j]
			j += 1
		k += 1
	
	# Copy the remaining elements
	# of L[], if there are any
	while i < len(L):
		arr[k] = L[i]
		i += 1
		k += 1
		
	# Copy the remaining elements
	# of R[], if there are any
	while j < len(R):
		arr[k] = R[j]
		j += 1
		k += 1
```
Example:
![[merge_sort_alg_example.png|700]]
___

**Bubble Sort:**
Bubble Sort is a simple sorting algorithm that repeatedly steps through the array, element by element, comparing the current element with the one after it, swapping their values if the former is larger than the latter. This algorithm has an average in-worse-case time complexity of $O(n^2)$.
```python
def bubbleSort(arr):
	n = len(arr)
	for i in range(n - 1):
		swapped = False
		for j in range(n - i - 1):
			if arr[j] > arr[j + 1]:
				arr[j], arr[j + 1] =
						arr[j + 1], arr[j]
				swapped = True
		if not swapped:
			breal
```
Example: rearrange elements in an array: [12, 15, 20, 29, 10, 14] = [10, 12, 14, 15, 20, 29]
___

**Insertion Sort**
Insertion sort is a simple sorting algorithm that builds the final sorted array on element at a time.
Like Bubble Sort, it does have an average in worst case time complexity of $O(n^2)$, however it's best case time complexity is $O(n)$. Where it makes fine choices to chose this algorithm, where data set is nearly sorted, but a poor choice when the data set is reversed.
```python
def insertionSort(arr):
	for i in range(1, len(arr)):
		key = arr[i]
		j = i - 1
		while j >= 0 and arr[j] > key:
			arr[arr + 1] = arr[j]
			j -= 1
		arr[j + 1] = key
```
Example: rearrange elements in an array: [11, 22, 33, 47, 4, 13, 26] = [4, 11, 13, 22, 26, 33, 47]
___


# Searching
A searching algorithm is a method or process used to find or retrieve an element from a data structure. The goal is to find whether an item exists in the data set, and oftentimes to determine its location.

**Linear Search**
Each element is checked in sequence until you find what you're looking for or the list ends. If the current element equals what we're looking for (X), return it.
```python
def linear_search(arr, x):
	for i in range(len(arr)):
		if arr[i] == x:
			return i
	return -1
```
The average worst case time complexity 


# Graph