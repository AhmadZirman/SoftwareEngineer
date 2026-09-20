[[SD02 - Classes_Events_Structure.pdf]]


# Exercise 2.1: Uncovering Objects and Events
Identify objects and events in the problem domain that are administrated, monitored or controlled in relation to F-klubbens bar.

For each identified object, argue why this belongs to the **problem domain**. Any objects that belongs to both the **problem domain** and the **application domain**? Argue why that is the case.

Make an **Event Table** to represent your findings and use the Affirmation Criteria to assess correctness of Classes and Events.

1. Objects identified
	- **Volunteer/Bartender** - person working behind the bar during opening hours
	- **Shift** - a scheduled period a volunteer works
	- **Member/Guest** - the person buying drinks
	- **Product** (beer, soda, snacks) - items sold across the counter
	- **Stock/Inventory** - quantity of each product on hand
	- **Sale** - a transaction where a product is exchanged for payment
	- **Tab** - a running credit balance for a member who pays later
	- **Price list** - current prices for products
	- **Delivery** - incoming stock from a supplier

2. Problem Domain vs. Application Domain
	**Problem Domain**
	- Product
	- Stock
	- Sale
	- Tab
	- Price list
	- Delivery
	- Shift

	**Application Domain**
	- Volunteer/Bartender (Register sales, updates stock)
	- Treasurer (reconciles tabs, adjusts prices)

	**Belongs to both**
	- Vounteer/Bartender: in the PD they are a data object (which shifts they've worked, which sales they registered); in the AD they are the active user who operates the register and updates stock during their shift. Same double role as Student/Lecturer in the Exercise 1.1 example.



3. Event Table

| **Events**    | Volunteer | Shift | Product | Sale | Tab |
| ------------- | --------- | ----- | ------- | ---- | --- |
| Signed up     | X         | X     |         |      |     |
| Started Shift | X         | X     |         |      |     |
| Sold          |           |       | X       | X    |     |
| Restocked     |           |       | X       |      |     |
| Paid          |           |       |         | X    | X   |
| Ran Out       |           |       | X       |      |     |

4. Applying the Affirmation Criteria
	- Sold, Paid, Restocked, Ran out: All instantaneous, atomic, clearly identifiable, and involve identifiable objects (Product+Sale, or Sale+Tab) → keep.
	- Started Shift, borderline: Is it an event (a moment)

![[Pasted image 20260920103546.png]]