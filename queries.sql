use test;
-- 3 queries that use aggregate functions

-- 1. Find the count of shows
SELECT COUNT(*) AS total_shows
FROM entertainment;

-- 2. The total number of pg-13 movies
SELECT COUNT(rating) AS total_pg13_rating
FROM entertainment
WHERE rating = 'PG-13';

-- 3. The number of pg movies per year
SELECT release_year, COUNT(rating) AS total_pg13_rating
FROM entertainment
WHERE rating = 'PG'
GROUP BY release_year;

-- 3 queries that use agg function with more than 1 table

-- 1. Listed genres and their total shows with the rating
SELECT g.genre_name, COUNT(e.show_id) AS total_shows, e.rating AS rating
FROM genre g
         JOIN entertainment_genre eg ON g.genre_id = eg.genre_id
         JOIN entertainment e ON eg.show_id = e.show_id
GROUP BY g.genre_name;

-- 2. Query that returns the average number of seasons per TV show
SELECT AVG(CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(ei.duration, ' ', 1), ' ', -1) AS INT)) AS average_seasons
FROM entertainment_info ei
WHERE ei.duration LIKE '% seasons';

-- 3. Query that return the count of the show directed by Jason Sterman
SELECT d.director_name, COUNT(e.show_id) AS total_shows
FROM director d
         JOIN entertainment_director ed ON d.director_id = ed.director_id
         JOIN entertainment e ON ed.show_id = e.show_id
WHERE d.director_name = 'Jack Hannah';

-- 3 queries that use subqueries

-- 1. All the shows that have the genre "comedy"
select * from entertainment_info
where show_id in (
    select show_id from entertainment_genre
    where genre_id = 4
);

-- 2. number of actors in each show
SELECT title, description,
       (SELECT COUNT(*) FROM entertainment_actor WHERE show_id = e.show_id) AS actor_count
FROM entertainment_info e;

-- 3. the number of movies for a genre
SELECT g.genre_name, movie_count
FROM (
         SELECT genre_id, COUNT(*) AS movie_count
         FROM entertainment_genre
         GROUP BY genre_id
     ) AS subquery
         JOIN genre g ON g.genre_id = subquery.genre_id;

-- 3 queries involving outer joins and aggregation functions

-- actor count per year
SELECT e.release_year, COUNT(a.actor_id) AS actor_count
FROM entertainment e
         LEFT OUTER JOIN entertainment_actor a ON e.show_id = a.show_id
GROUP BY e.release_year;

-- average duration of movies per year
SELECT e.release_year, AVG(CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(ei.duration, ' ', 1), ' ', -1) AS INT)) AS average_duration
FROM entertainment e
         LEFT OUTER JOIN entertainment_info ei ON e.show_id = ei.show_id
WHERE e.rating = 'PG' AND ei.duration LIKE '% min'
GROUP BY e.release_year;

-- average duration of shows per rating
SELECT e.rating, AVG(CAST(SUBSTRING_INDEX(ei.duration, ' ', 1) AS INT)) AS average_duration
FROM entertainment e
         LEFT OUTER JOIN entertainment_info ei ON e.show_id = ei.show_id
GROUP BY e.rating;

-- 3 queries involving set operators

-- combines the results for 2020 and 2021 into one set
SELECT show_id, rating
FROM entertainment
WHERE release_year = '2020'
UNION
SELECT show_id, rating
FROM entertainment
WHERE release_year = '2021';

-- using INTERSECT to find the common records between two tables,
-- basically the movies in 2021 that were comedy
SELECT show_id, rating
FROM entertainment
WHERE release_year = '2021'
    INTERSECT
    SELECT show_id, rating
    FROM entertainment
    WHERE show_id IN (
        SELECT show_id
        FROM entertainment_genre
        WHERE genre_id = 4
    );

-- using the EXCEPT operator to find the records in the first table that are not in the second table
SELECT show_id, rating
FROM entertainment
WHERE release_year = '2021'
EXCEPT
SELECT show_id, rating
FROM entertainment
WHERE show_id IN (
    SELECT show_id
    FROM entertainment_genre
    WHERE genre_id = 4
);

-- comparing execution time of queries
analyze format = json
    SELECT *
    FROM entertainment e
             JOIN entertainment_info ei ON e.show_id = ei.show_id;

ANALYZE format = json
    SELECT *
    FROM entertainment e
             LEFT OUTER JOIN entertainment_info ei ON e.show_id = ei.show_id;

ANALYZE format = json
    SELECT *
    FROM entertainment e
             CROSS JOIN entertainment_info ei;

-- extra queries

SELECT e.show_id, e.release_year, e.rating, ei.title
FROM entertainment e
         JOIN entertainment_director ed ON e.show_id = ed.show_id
         JOIN director d ON ed.director_id = d.director_id
         JOIN entertainment_info ei ON e.show_id = ei.show_id
WHERE d.director_name = 'Jason Sterman';

SELECT e.show_id, e.release_year, e.rating, ei.title
FROM entertainment e
         JOIN entertainment_genre eg ON e.show_id = eg.show_id
         JOIN genre g ON eg.genre_id = g.genre_id
         JOIN entertainment_info ei ON e.show_id = ei.show_id
WHERE g.genre_name = 'Comedy';

SELECT e.show_id, e.release_year, e.rating, ei.title
FROM entertainment e
         JOIN entertainment_info ei ON e.show_id = ei.show_id
WHERE e.rating = 'PG-13';

SELECT e.show_id, e.release_year, e.rating, ei.title
FROM entertainment e
         JOIN entertainment_actor ea ON e.show_id = ea.show_id
         JOIN actor a ON ea.actor_id = a.actor_id
         JOIN entertainment_info ei ON e.show_id = ei.show_id
WHERE a.actor_name = 'Johnny Depp';