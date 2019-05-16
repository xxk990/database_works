//Ke Xu
//CWID: 10430968
//part1 is about to (1) the customer's average sale of this productfor the state
//(2) the average sale of the product and the customer but forthe other statesand
//(3) the customer’s average sale for the given state, but for the other products
//And then output the result as a table
//For this part I use same method as in last homework(part2). To create a Hashmap as our table. To store all information in the table.

import java.sql.*;
import java.util.HashMap;

public class part1 {
    //object to store the value

    static class sales{


        int count;               //count the number of each combination
        int othercount;          //counter for the products in different states
        int otherprodcount;      //counter for same state different product

        int sum;                 //sum for same state and same product
        int othersum;            //sum for different state and same product
        int otherprodsum;        //sum for same state and different product

        float avg;               //avg for same state and same product
        float otheravg;          //avg for different state and same product
        float othercustavg;      //avg for same state and different product

    }

    // product and customer combination
    static class custProd {
        String customer;
        String product;
        String state;
        //find the hashcode for the combination
        public int hashCode() {
            return (customer.hashCode() + product.hashCode() + state.hashCode());
        }

        //check the combination of the product and customer is same or not
        public boolean equals(Object obj) {
            if (obj instanceof custProd) {
                custProd combi1 = (custProd)obj;
                if (customer.equals(combi1.customer) && product.equals(combi1.product) && state.equals(combi1.state))
                    return true;
            }
            return false;
        }
    }

    public static void main(String[] args)
    {
        String usr ="postgres";
        String pwd ="dante110";
        String url ="jdbc:postgresql://localhost:5432/postgres";
        //the map to store all result
        HashMap<custProd, sales> salesMap = new HashMap<custProd, sales>();
        //create a label
        String label = String.format("%-10s", "CUSTOMER") + String.format("%-10s", "PRODUCT") + String.format("%-10s","STATE" )+ String.format("%-10s","CUST_AVG" )+ String.format("%-10s","OTHER_STATE_AVG" )+ " " +  String.format("%-10s","OTHER_PROD_AVG" );
        try
        {
            Class.forName("org.postgresql.Driver");
            System.out.println("Success loading Driver!");
        }

        catch(Exception e)
        {
            System.out.println("Fail loading Driver!");
            e.printStackTrace();
        }

        try
        {
            Connection conn = DriverManager.getConnection(url, usr, pwd);
            System.out.println("Success connecting server!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");




            while (rs.next())
            {
                sales sales1;

                String customer = rs.getString("cust");
                String product = rs.getString("prod");
                String state = rs.getString("state");

                custProd comb = new custProd();
                comb.product = product;
                comb.customer = customer;
                comb.state = state;
                int quant = rs.getInt("quant");

                //find the sum for each combination
                if(!salesMap.containsKey(comb)){
                    sales1 = new sales();
                    sales1.sum = quant;
                    sales1.count =1;
                    sales1.othercount=0;
                    sales1.otherprodcount=0;
                    sales1.othersum=0;
                    sales1.otherprodsum=0;
                    sales1.avg = 0;
                    salesMap.put(comb, sales1);
                }else {
                    sales1 = salesMap.get(comb);
                    sales1.sum = sales1.sum + quant;
                    sales1.count= sales1.count+1;

                }

            }

            //compute the sum of other two situations(same product and different state, same state and different product)
            for (custProd key1 : salesMap.keySet()) {
                for (custProd key2 : salesMap.keySet()) {
                    sales value1 = salesMap.get(key1);
                    sales value2 = salesMap.get(key2);
                    //same product and different state
                    if(key1.customer.equals(key2.customer) && key1.product.equals(key2.product) && !key1.state.equals(key2.state) ) {
                        value2.othersum = value2.othersum + value1.sum;
                        value2.othercount=value2.othercount+value1.count;

                    }
                    //same state and different product
                    if(key1.customer.equals(key2.customer) && !key1.product.equals(key2.product) && key1.state.equals(key2.state) ) {
                        value2.otherprodsum = value2.otherprodsum + value1.sum;
                        value2.otherprodcount=value2.otherprodcount+value1.count;
                    }

                }
            }
            System.out.println(label);
            for (custProd key : salesMap.keySet()) {
                sales value = salesMap.get(key);
                //avg. for these three tables
                value.avg = (float)value.sum/value.count;
                value.otheravg = (float)value.othersum/value.othercount;
                value.othercustavg = (float)value.otherprodsum/value.otherprodcount;
                //print the result
                String s = String.format("%-10s", key.customer) + " " + String.format("%-10s", key.product) + " " + String.format("%-10s", key.state)+ " " + String.format("%-10s", value.avg)+ " " + String.format("%-10s", value.otheravg)+ " " + String.format("%-10s", value.othercustavg);
                System.out.println(s);

            }

        }
        catch(SQLException e)
        {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
