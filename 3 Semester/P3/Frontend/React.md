
# How the frontend works
React builds the page out of small pieces called components. The browser loads `index.html`, which has one empty `<div id="root">`. `main.tsx` tells React to draw the whole inside that div.

```markdown

index.html  →  main.tsx  →  RouterProvider (router.tsx)
                              ├─ "/"        → CustomerLayout → Frontpage / Menupage / LogIn
                              ├─ "/waiter"  → StaffLayout    → WaiterPage
                              └─ "/kitchen" → StaffLayout    → Overview / Pizza / Grill

```
