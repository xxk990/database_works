--Ke Xu
--CWID: 10430968
-- For each customer, productand state combination, compute (1) the customer's average sale of this productfor the state
-- (2) the average sale of the product and the customer but forthe other statesand 
-- (3) the customer’s average sale for the given state, but for the other products

--the customer's average sale of this productfor the state
with avgcurrent as(
select s1.cust, s1.prod, s1.state, avg(s2.quant) CUST_AVG
from sales s1, sales s2
where s1.cust = s2.cust and s1.prod = s2.prod and s1.state = s2.state
group by s1.cust, s1.prod, s1.state
),
--the average sale of the product and the customer but forthe other states
avg_other_state as(
select s1.cust, s1.prod, s1.state, avg(s2.quant) OTHER_STATE
from sales s1, sales s2
where s1.cust = s2.cust and s1.prod = s2.prod and s1.state != s2.state
group by s1.cust, s1.prod, s1.state
),
--the customer’s average sale for the given state, but for the other products
avg_other_prod as(
select s1.cust, s1.prod, s1.state, avg(s2.quant) OTHER_PROD
from sales s1, sales s2
where s1.cust = s2.cust and s1.prod != s2.prod and s1.state = s2.state
group by s1.cust, s1.prod, s1.state
),

--join avgcurrent and avg_other_staet two tables
final1 as(
select distinct * from avgcurrent natural full outer join avg_other_state
),
--final result
final2 as(
select distinct* from final1 natural full outer join avg_other_prod
)

select cust,prod,state,CUST_AVG,OTHER_STATE,OTHER_PROD
from final2
order by cust,prod