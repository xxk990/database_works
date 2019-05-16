--This query is about to find the max. quantity and min. quantity for each customer with correspending date and state.


--find max. quantity, min. quantity and average quantity
with part1 as 
(
select cust, max(quant) max_q, min(quant) min_q, avg(quant) avg_q
from sales
group by cust
),

--create a table for max. quantity and corresponding date with state for each customer
part2  as 
(
select p.cust, p.max_q, s.prod, s.month, s.day, s.year, s.state, p.avg_q
from part1 p, sales s
where p.cust = s.cust
  and p.max_q = s.quant
),


--create a table for min. quantity and corresponding date with state for each customer
part3 as
(
select p.cust, p.min_q, s.prod,s.month, s.day, s.year, s.state, p.avg_q
from part1 p, sales s
where p.cust = s.cust
  and p.min_q = s.quant
  )

--combinate part2 and part3 to output the final result
select p2.cust, p3.min_q, p3.prod,p3.month, p3.day, p3.year, p3.state, p2.max_q, p2.prod, p2.month, p2.day, p2.year, p2.state,  p2.avg_q
from part2 p2, part3 p3
where p2.cust = p3.cust
