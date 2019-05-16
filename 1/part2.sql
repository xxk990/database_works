--Ke Xu
--Id: 10430968
--part2
--This query is about to find the max. quantity for New Jersey, Min. quantity for New York and Min. quantity for Connecticut for each combination between customer and product with corresponding date.

--table to store all the combination between customer and products
with prodCust as(
select distinct cust, prod
from sales
),

--find the max. in New Jersey for each combination
NJMax as(
select distinct cust, prod, max(quant) NJ_Max
from sales natural full outer join prodCust
where state = 'NJ' and year<2009
group by cust, prod
),

--find the min. in New York for each combination 
NYMin as(
select distinct cust, prod, min(quant) NY_Min
from sales natural full outer join prodCust
where state = 'NY' and year<2009
group by cust, prod
),

--find the min. in Connecticut for each combination
CTMin as(
select distinct cust, prod, min(quant) CT_Min
from sales natural full outer join prodCust
where state = 'CT'
group by cust, prod
),

--make a new table to store the max. in New Jersey for each combination and corresponding date 
NJMax1 as(
select nj.cust, nj.prod, nj.NJ_Max, s.month NJ_month, s.day NJ_day, s.year NJ_year
from  NJMax nj, sales s
where nj.cust = s.cust and nj.NJ_Max = s.quant
	),

--make a new table to store the min. in New York for each combination and corresponding date 
NYMin1 as(
select ny.cust, ny.prod, ny.NY_Min, s.month NY_month, s.day NY_day, s.year NY_year
from  NYMin ny, sales s
where ny.cust = s.cust and ny.NY_Min = s.quant
	),
	
--make a new table to store the min. in Connecticut for each combination and corresponding date 
CTMin1 as(
select ct.cust, ct.prod, ct.CT_Min, s.month CT_month, s.day CT_day, s.year CT_year
from  CTMin ct, sales s
where CT.cust = s.cust and ct.CT_Min = s.quant
	),
	
--full outer join table NJMax1 and NYmin1
result1 as(
select *
from  NJMax1
natural full outer join NYmin1
	),

--full outer join table result1 and CTmin1(this will be the final result join NJMax, NYmin1 and CTmin1 three tables)
result2 as(
select *
from result1
natural full outer join CTMin1
order by cust
)

--output the final result
select * from result2


