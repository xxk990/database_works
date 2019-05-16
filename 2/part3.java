//Ke Xu
//part2 is about: for customer and product, to find the average sales before and after each month
//I use three arrays to store count for every month, sum for every month and avg for every month. And the index of the array will be the month
//if the the month doesn't have before month, after month or before month ,after month don't have any value it will print null
//I store the result in hashmap as my table.
import java.sql.*;
import java.util.HashMap;

public class part3 {
    //object to store the value

    static class sales{
        //half sum
        int halfsum;
        //store sum for 12 months
        int[] month = new int[12];
        //store the month that just greater than the halfsum
        int minmonth;
    }

    // product and customer combination
    static class custProd {
        String customer;
        String product;

        //find the hashcode for the combination
        public int hashCode() {
            return (customer.hashCode() + product.hashCode());
        }

        //check the combination of the product and customer is same or not
        public boolean equals(Object obj) {
            if (obj instanceof custProd) {
                custProd combi1 = (custProd)obj;
                if (customer.equals(combi1.customer) && product.equals(combi1.product))
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
        String result="";
        String label = String.format("%-10s", "CUSTOMER") + " " +String.format("%-10s", "PRODUCT") + " " + String.format("%-10s", "1/2 PURCHASED BY MONTH");
        int min = 0;
        int temsum = 0;
        //create a label
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
                int month = rs.getInt("month");
                int quant = rs.getInt("quant");

                custProd comb = new custProd();
                comb.product = product;
                comb.customer = customer;

                //comput the sum for each month of each combination
                if(!salesMap.containsKey(comb)){
                    sales1 = new sales();
                    for(int i=0; i<12; i++) {
                        sales1.month[i] = 0;
                    }
                    sales1.halfsum=quant;
                    sales1.month[month-1] = quant;
                    salesMap.put(comb, sales1);
                }else {
                    sales1 = salesMap.get(comb);
                    sales1.month[month-1] = sales1.month[month-1]+quant;
                    sales1.halfsum= sales1.halfsum + quant;
                }

            }

            System.out.println(label);
            //find the month that just greater than hal sum
            for (custProd key : salesMap.keySet()) {
                sales value = salesMap.get(key);
                value.halfsum = value.halfsum/2;
                temsum = 0;
                //cumulative sum
                for (int n =0;n<12;n++){
                    value.month[n] = value.month[n]+temsum;
                    temsum = value.month[n];
                }

                //distance between the sum and halfsum
                for (int j =0;j<12;j++){
                    value.month[j] = value.month[j]-value.halfsum;
                }

                //find the min. distance that will be the result
                min = 9999999;
                for (int m =0; m<12; m++){
                    if(value.month[m]>=0) {
                        if (value.month[m] < min) {
                            min = value.month[m];
                            value.minmonth = m;
                        }
                    }
                }

                System.out.println(String.format("%-10s", key.customer) + " " + String.format("%-10s", key.product)+ " " + String.format("%-10s", value.minmonth+1));
            }

        }
        catch(SQLException e)
        {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
