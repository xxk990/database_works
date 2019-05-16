//Ke Xu
//CWID: 10430968
//part2 is about: for customer and product, to find the average sales before and after each month
//I use three arrays to store count for every month, sum for every month and avg for every month. And the index of the array will be the month.
//if the the month doesn't have before month, after month or before month ,after month don't have any value it will print null
//I store the result in hashmap as my table.

import java.sql.*;
import java.util.HashMap;

public class part2 {
    //object to store the value
    static class sales{
        //counter for 12 months
        int[] count = new int[12];
        //avg for 12 months
        float[] avg = new float[12];
        //sum for 12 months
        int[] month = new int[12];

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
        String lable = String.format("%-10s", "CUSTOMER") + " " + String.format("%-10s", "PRODUCT") + " " +String.format("%-10s", "MONTH") + " "+String.format("%-10s","BEFORE_MONTH") + " " +String.format("%-10s", "AFTER_MONTH");
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

                //sum for each combination and each month
                if(!salesMap.containsKey(comb)){
                    sales1 = new sales();
                    for(int i=0; i<12; i++) {
                        sales1.month[i] = 0;
                        sales1.avg[i] = 0;
                        sales1.count[i]=0;
                    }
                    sales1.month[month-1] = quant;
                    sales1.count[month-1] = 1;
                    salesMap.put(comb, sales1);
                }else {
                    sales1 = salesMap.get(comb);
                    sales1.month[month-1] = sales1.month[month-1]+quant;
                    sales1.count[month-1]= sales1.count[month-1]+1;
                }
            }

            //print the table
            System.out.println(lable);
            for (custProd key : salesMap.keySet()) {
                sales value = salesMap.get(key);
                //avg. for each combination
                for(int n = 0; n<12; n++){
                    value.avg[n] = (float)value.month[n]/value.count[n];
                }

                //print the result
                for(int m = 0; m<12; m++) {
                    result = String.format("%-10s", key.customer) + " " + String.format("%-10s", key.product)+ " " + String.format("%-10s", m+1);
                    //January before_month will be null, December after_month will be null
                    if(m==0&&!Float.isNaN(value.avg[m+1]))
                        result = result +  " " + String.format("%-10s", "null")+ " " + String.format("%-10s", value.avg[m+1]);
                    else if(m==0&&Float.isNaN(value.avg[m+1]))
                        result = result +  " " + String.format("%-10s", "null")+ " " + String.format("%-10s", "null");
                    else if(m==11&&!Float.isNaN(value.avg[m-1]))
                        result = result +  " " + String.format("%-10s", value.avg[m-1])+ " " + String.format("%-10s", "null");
                    else if(m==11&&Float.isNaN(value.avg[m-1]))
                        result = result +  " " + String.format("%-10s", "null")+ " " + String.format("%-10s", "null");

                    //if the value is NaN print null
                    if(m>0 && m<11) {
                        if(!Float.isNaN(value.avg[m-1] ))
                            result = result +  " " + String.format("%-10s", value.avg[m-1]);
                        else if(Float.isNaN(value.avg[m-1]))
                            result = result +  " " + String.format("%-10s", "null");
                        if(!Float.isNaN(value.avg[m+1]))
                            result = result +" " + String.format("%-10s", value.avg[m+1]);
                        else if(Float.isNaN(value.avg[m+1]))
                            result = result +" " + String.format("%-10s", "null");
                    }


                    System.out.println(result);
                }

            }

        }
        catch(SQLException e)
        {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
