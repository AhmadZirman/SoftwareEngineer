
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
A component is a function that returns 