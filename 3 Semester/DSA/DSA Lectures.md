# Set up folder in Docker with Terminal


#### Step 0: Get docker running
```BASH
docker --version
docker compose version
```

#### Step 1: Folder and Terminal
1. Unzip the folder `Extras-xxxxx.zip` to somewhere easy, e.g. `Desktop/DSA/Exercises/Extras-xxxxx`
2. Go into that folder in terminal
```BASH
cd ~/Desktop/DSA/Exercises/Extras-xxxxx
ls
```
You should see a list of what the folder contains

#### Step 2: Start it
```BASH
docker compose up -d
```
(`-d`) means "run in the background"

Then check that both container are up:
```BASH
docker compose ps
```

To confirm the SQL files ran:
```BASH
docker compose logs db
```


#### Step 3: Get inside the database
```BASH
docker compose exec db psql -U dbs -d dbs
```
Your prompt changes to `dbs=#`, means you're now talking SQL to P



### Lecture 1, Slides, Exercises
[[Software Engineering/3 Semester/DSA/Notes/Lecture 1|Lecture 1]]
[[Software Engineer/3 Semester/DSA/Slides/Lecture1-Intro-RelationalModel-typst.pdf|Slides]]
[[Software Engineer/3 Semester/DSA/Exercises/Lecture 1 Exercises|Exercise 1]]

### Lecture 2, Slides, Exercises
[[Software Engineering/3 Semester/DSA/Notes/Lecture 2|Lecture 2]]
[[Software Engineer/3 Semester/DSA/Slides/Lecture2-ER-Diagrams.pdf|Slides]]
[[Software Engineer/3 Semester/DSA/Exercises/Lecture 2 Exercise|Exercise 2]]

# Lecture 3, Slides, Exercises
[[Lecture 3]]
[[Lecture3-ER-to-SQL.pdf|Slides]]
