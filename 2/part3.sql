--Ke XU
--This query is about for customer and product, find the monthby which time, a halfof the sales quantities have been purchased.

--sum for each combination of cust, product, and month
with sum1 as(
select cust, prod, month, sum(quant)
from sales
group by cust,prod,month
order by month
),
--cumulative sum
cumulative as(
select s1.cust,s1.prod,s1.month, sum(s2.sum)
from sum1 s1 join sum1 s2 on s1.month >= s2.month
where s1.cust = s2.cust and s1.prod=s2.prod
group by s1.cust,s1.prod,s1.month
order by s1.month
),
--find half of the sum for each cust and prod combinaion
halfsum as(
select cust, prod, sum(quant)/2 hs
from sales
group by cust, prod
),
--find distance between each value of month and half sum
distance as(
select c.cust, c.prod,c.month, c.sum-hs dis
from cumulative c, halfsum h
where c.cust = h.cust and c.prod = h.prod
),
--find the min. distance
min1 as(
select d.cust, d.prod, min(d.dis) mins1
from distance d
where d.dis>=0
group by d.cust, d.prod
),
--find min month
final as(
select m.cust, m.prod, d.month "1\2 PURCHASED BY MONTH "
from min1 m, distance d
where m.mins1 = d.dis
order by d.prod
)
select * from final;
