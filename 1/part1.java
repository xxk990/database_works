//Ke Xu

//This is the program for part1. Before execute the program you need to modify the username and password to yours.
//I use a hashMap to store all the information I need. I use customer name as a key in the map. It is easy to find or point different element in the
// map with a customer name. Thus, I chose hashMap to do this problem
//For the max and min I initial them to a value(quant) from the database. Max: if the quant. > Max. then set Max = quant. Min: if quant < Min. then
//set Min. = quant. until scan every line in the database.
//object 'sales' is I created to store the information except customer name.

import java.sql.*;
import java.util.HashMap;

public class part1 {
    //object to store the value
    static class sales {
         String maxPro; //product
         String minPro; //product
         int maxDay;
         int maxMonth;
         int maxYear;
         int minDay;
         int minMonth;
         int minYear;
         String maxSt;
         String minSt;
         int maxQuant;
         int minQuant;
         int sum;
         int count;     //count the number of each customer
         float avg;

         //convert the sales object tot a string
         public String toString(){
               String s;
               String maxDate = String.format("%02d",maxMonth) + "/" + String.format("%02d",maxDay) + "/" + maxYear;
               String minDate = String.format("%02d",minMonth) + "/" + String.format("%02d", minDay) + "/" + minYear;
               s= String.format("|%-10s|", minQuant) +String.format("%-10s|", minPro)+  String.format("%-10s|", minDate) + String.format("%5s|", minSt) +String.format("%5s|", maxQuant)+ String.format("%10s|", maxPro) + String.format("%10s|", maxDate) + String.format("%5s|", maxSt)+String.format("%10s|",avg);
             return s;
         }

    }

    public static void main(String[] args)
    {
        String usr ="postgres";
        String pwd ="dante110";
        String url ="jdbc:postgresql://localhost:5432/postgres";
        //the map to store all result
        HashMap<String, sales> salesMap = new HashMap<String, sales>();
        //create a label
        String label = String.format("%-11s", "Customer")+  String.format("|%-10s|", "Min.") + String.format("%-10s|", "Min.Prod")
                     + String.format("%-10s|", "Min.Date") + String.format("%5s|", "Min.st")
                     + String.format("%5s|", "Max.")+ String.format("%10s|", "Max.prod")
                     + String.format("%7s|", "Max.date") + String.format("%7s|", "Max.st")+ String.format("%8s|","avg.");
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

            int quant;
            sales sales1;

            while (rs.next())
            {
                //if the salesMap doesn't contain customer then create one, and set all the information.
                if(!salesMap.containsKey(rs.getString("cust"))) {
                    sales1 = new sales();
                    sales1.maxDay = rs.getInt("day");
                    sales1.maxMonth = rs.getInt("month");
                    sales1.maxPro = rs.getString("prod");
                    sales1.maxQuant = rs.getInt("quant");
                    sales1.maxSt = rs.getString("state");
                    sales1.maxYear = rs.getInt("year");
                    sales1.minDay = rs.getInt("day");
                    sales1.minMonth = rs.getInt("month");
                    sales1.minPro = rs.getString("prod");
                    sales1.minQuant = rs.getInt("quant");
                    sales1.minSt = rs.getString("state");
                    sales1.minYear = rs.getInt("year");
                    sales1.sum = rs.getInt("quant");
                    sales1.count = 1;
                    salesMap.put(rs.getString("cust"), sales1);
                }
                else{
                    //if the customer is in the map, then find the max and min with the corresponding date and state
                    quant = rs.getInt("quant");;
                    sales1  = salesMap.get(rs.getString("cust"));

                    //if the customer exists in map then count add 1
                    sales1.count++;

                    //find the sum of all quant for each customer
                    sales1.sum = sales1.sum + quant;

                    //find the max
                    if(sales1.maxQuant < quant){
                        sales1.maxQuant = quant;
                        sales1.maxYear = rs.getInt("year");
                        sales1.maxDay = rs.getInt("day");
                        sales1.maxMonth = rs.getInt("month");
                        sales1.maxSt = rs.getString("state");
                        sales1.maxPro = rs.getString("prod");
                    }

                    //find the min
                    if(sales1.minQuant > quant){
                        sales1.minQuant = quant;
                        sales1.minYear = rs.getInt("year");
                        sales1.minDay = rs.getInt("day");
                        sales1.minMonth = rs.getInt("month");
                        sales1.minSt = rs.getString("state");
                        sales1.minPro = rs.getString("prod");
                    }
                }

            }

            //compute the avg. and print the result as a table.
            System.out.println(label);
            for(String key: salesMap.keySet()) {
            sales value = salesMap.get(key);
            value.avg = (float)value.sum/value.count;    //Average.
            System.out.println(String.format("%-10s",key) + " " + value.toString());
        }


        }

        catch(SQLException e)
        {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
