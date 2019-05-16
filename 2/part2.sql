--Ke Xu
--CWID: 10430968
--This query is bout for customer and product, show the average sales before and after each month.


--find combination of cust, and prod
with comb_cust_prod as(
select distinct s1.cust, s2.prod
from sales s1
cross join sales s2
),
--find combination of cust, prod, and month
comb_withmonth as(
select distinct c.cust, c.prod, s.month
from comb_cust_prod c
cross join sales s
order by s.month
),
--find avg. of each combination
avg_each_comb as(
select cust, prod, month, avg(quant)
from sales
group by cust,prod,month
),

--creat table for after month avg.
after_month as(
select distinct p1.cust, p1.prod, p2.month, round(p1.avg,2) after_month
from avg_each_comb p1, comb_withmonth p2
where p1.cust = p1.cust and p1.prod = p2.prod and p1.month = p2.month+1
order by p2.month
),
--creat table for before month avg.
before_month as(
select distinct p1.cust, p1.prod, p2.month, round(p1.avg,2) before_month
from avg_each_comb p1, comb_withmonth p2
where p1.cust = p1.cust and p1.prod = p2.prod and p1.month = p2.month-1
order by p2.month
),
--join two table to get final result
final1 as(
select distinct * from after_month natural full outer join comb_withmonth
),
final2 as(
select distinct * from final1 natural full outer join before_month
)
select cust,prod,month,before_month,after_month 
from final2 
order by cust, prod,month

