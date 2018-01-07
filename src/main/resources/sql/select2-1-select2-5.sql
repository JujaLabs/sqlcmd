-- Find the names of buyers, whose age is from 16 to 35
SELECT c.name
FROM customers c
WHERE c.age BETWEEN 16 AND 35;

-- Find the names of buyers whose surname ends in 'OV'
SELECT c.name
FROM customers c
WHERE c.surname ILIKE '%ov';

-- Find the name of the most expensive item for purchase,
-- in the name of which there is the letter 'V', but not the first and not the last one.
SELECT
  products.name,
  MAX(products.purchase_price)
FROM (SELECT *
      FROM products p
      WHERE p.name ~* '^([^v])+([v]).+([^v])$') AS products
WHERE products.purchase_price = (SELECT MAX(purchase_price)
                          FROM products
                          WHERE name ~* '^([^v])+([v]).+([^v])$')
GROUP BY products.name;

--Find the names of buyers whose names contain the letter 'V' and no more than two times
--For a regular expression, thanks to @kortov
SELECT c.name
FROM customers c
WHERE c.name SIMILAR TO '([^Vv]*[Vv][^Vv]*){0,2}';

-- Find the names of buyers whose name length is more than 3 characters and 4 character are 'O' (Latin letter O)
-- and age less than 50 years.
SELECT c.name
FROM customers c
WHERE (c.name ILIKE '___O%') AND (c.age < 50)

