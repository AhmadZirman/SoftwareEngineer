
# How the frontend works
React builds the page out of small pieces called components. The browser loads `index.html`, which has one empty `<div id="root">`. `main.tsx` tells React to draw the whole inside that div.

```md

	index.html  →  main.tsx  →  RouterProvider (router.tsx)
	
	router.tsx
	  ├─ "/"        → CustomerLayout → Frontpage / Menupage / LogIn
	  ├─ "/waiter"  → StaffLayout    → WaiterPage
	  └─ "/kitchen" → StaffLayout    → Overview / Pizza / Grill

```
The router looks at the URL and picks which components to show. The layouts (`CustomerLayou`, `StaffLayout`) draw the header and menu bar, and `<Outlet />` is the slot where the current page appears.

The basic rule of React: data goes in, UI comes out. When the data changes, React redraws the parts of the page that changed by itself.. You never write `document.getElementById(...)`.


# Components
A component is a function that returns HTML-like code (JSX). The name starts with a capital letter.

```tsx
	
	// pages/kitchen/PizzaScreen.tsx
	export default function PizzaScreen() { return <KitchenBoard station="PIZZA" 
	title="PIZZA KITCHEN" />; }
	
```
Good example to show: **reuse.** `KitchenBoard` is written once and used in three places:
- `PizzaScreen` shows the pizza board
- `GrillScreen` shows the grill board
- `OverviewScreen` shows both side by side
You write once, change it in one place, and every screen gets the change.


# Props
Props are **the information a component gets from outside**, like parameters to a function. They only go one way, from parent to child, and the child can't change them.

```tsx
	
	// features/waiter/components/TableCard.tsx
	type Props = { table: RestaurantTable; // data
		onClick: () => void; // a function
	};
	
	export default function TableCard({ table, onClick }: Props) { 
		return (
		<button onClick={onClick}>
			<div>{table.tableNumber}</div>
			<p>{table.seats} pladser</p>
		</button>
		);
	}
	
```
There are two kinds of props:
- **Data props** like `table`, `items` and `lines`. These say what to show.
- **Function props** like `onClick`, `onSend` and `onRemove`. These say what to do when something happens. This is how a child "talks back" to its parent: it calls a function the parent handed it.

`OrderPanel` is the clearest example. It gets the order lines and five functions (`onChangeQuantity`, `onRemove`, `onNote`, `onSend`, `onCancel`). It doesn't store or change the order itself. It just shows it and calls the parent's functions when buttons are pressed.

TypeScript's `type Props` checks that everyone passes the right things. If you forget `onClick`, you get an error before the app even runs.


# State
State is **data that belongs to a component and can change over time**. When state changes, React redraws the component automatically.

`WaiterPage.tsx` has three pieces of state:

```tsx
	
	const [selectedTable, setSelectedTable] = useState<number | null>(null);
	// which table is open
	
	const [category, setCategory] = useState(categories[0]);
	// which menu tab is active
	
	const [orders, setOrders] = useState<Record<number, OrderLine[]>>({});
	// order lines per table
	
```
`useState` gives back two things: **the value** and **a function to change it**.

Example flow, from the waiter tapping the table 4:
1. `TableCard` calls `onClick`
2. That runs `setSelectedTable(4)`
3. React notices the state changes and redraws `WaiterPage`
4. `SelectedTable !== null`, so the order screen is shown instead of the table overview

Two important rules, both of which the code follows:
- **Never change state directly.** Always make a copy with the change: `setOrder({...orders, [selectedTable]: newLines })`. The `...` (spread) copies the old object.
  React only notices the change if you give it a new object.
- **State lives in the parent and goes down as props.** `WaiterPage` owns `orders`, and `OrderPanel` only gets `lines` plus functions. This is called "lifting state up", and it means there's only one place where the order can be changed.

**Props vs state in one sentence:** props are given to you from outside and are read-only, while state is your own and can change.


# Hooks
Hooks are special functions that start with `use`. They give a component extra abilities, like remembering things or fetching data. Rules: only call them **at the top of a component** (not inside `if` or loops), and only inside components or other hooks.

**Built-in React hook:**
- 